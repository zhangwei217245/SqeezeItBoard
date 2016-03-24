package squeezeboard;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static squeezeboard.model.GameUtils.file_manual;

/**
 * Created by zhangwei on 3/21/16.
 */
public class TutorialViewrController {
    @FXML
    private Pagination pagination ;
    @FXML private Label currentZoomLabel ;

    private FileChooser fileChooser ;
    private ObjectProperty<PDFFile> currentFile ;
    private ObjectProperty<ImageView> currentImage ;
    @FXML  private ScrollPane scroller ;

    @FXML private Button btn_zoom_fit;
    @FXML private Button btn_zoom_width;

    private DoubleProperty zoom ;
    private PageDimensions currentPageDimensions ;

    private ExecutorService imageLoadService ;

    private static final double ZOOM_DELTA = 1.05 ;


    // ************ Initialization *************

    public void initialize() {

        createAndConfigureImageLoadService();
//        createAndConfigureFileChooser();

        currentFile = new SimpleObjectProperty<>();
        //updateWindowTitleWhenFileChanges();

        currentImage = new SimpleObjectProperty<>();
        scroller.contentProperty().bind(currentImage);
        scroller.contentProperty().addListener((observable, oldValue, newValue) -> {
            scroller.setVvalue(scroller.getVmin());
            btn_zoom_width.fire();
        });
//        scroller.setFitToHeight(true);
//        scroller.setFitToWidth(true);

        scroller.setPannable(true);


        zoom = new SimpleDoubleProperty(1);
        // To implement zooming, we just get a new image from the PDFFile each time.
        // This seems to perform well in some basic tests but may need to be improved
        // E.g. load a larger image and scale in the ImageView, loading a new image only
        // when required.
        zoom.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateImage(pagination.getCurrentPageIndex(), true);
            }
        });

        currentZoomLabel.textProperty().bind(Bindings.format("%.0f %%", zoom.multiply(100)));

        bindPaginationToCurrentFile();
        createPaginationPageFactory();

        loadFile(SqueezeBoard.class.getResourceAsStream(file_manual));
    }

    @FXML
    private void handleZoom(ActionEvent e) {
        System.out.println(e.getTarget());
    }

    private void createAndConfigureImageLoadService() {
        imageLoadService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

//    private void createAndConfigureFileChooser() {
//        fileChooser = new FileChooser();
//        fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf", "*.PDF"));
//    }

//    private void updateWindowTitleWhenFileChanges() {
//        currentFile.addListener(new ChangeListener<PDFFile>() {
//            @Override
//            public void changed(ObservableValue<? extends PDFFile> observable, PDFFile oldFile, PDFFile newFile) {
//                try {
//                    String title = newFile == null ? "PDF Viewer" : newFile.getStringMetadata("Title") ;
//                    Window window = pagination.getScene().getWindow();
//                    if (window instanceof Stage) {
//                        ((Stage)window).setTitle(title);
//                    }
//                } catch (IOException e) {
//                    showErrorMessage("Could not read title from pdf file", e);
//                }
//            }
//
//        });
//    }

    private void bindPaginationToCurrentFile() {
        currentFile.addListener(new ChangeListener<PDFFile>() {
            @Override
            public void changed(ObservableValue<? extends PDFFile> observable, PDFFile oldFile, PDFFile newFile) {
                if (newFile != null) {
                    pagination.setCurrentPageIndex(0);
                }
            }
        });
        pagination.pageCountProperty().bind(new IntegerBinding() {
            {
                super.bind(currentFile);
            }
            @Override
            protected int computeValue() {
                return currentFile.get()==null ? 0 : currentFile.get().getNumPages() ;
            }
        });
        pagination.disableProperty().bind(Bindings.isNull(currentFile));
    }

    private void createPaginationPageFactory() {
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageNumber) {
                if (currentFile.get() == null) {
                    return null ;
                } else {
                    if (pageNumber >= currentFile.get().getNumPages() || pageNumber < 0) {
                        return null ;
                    } else {
                        updateImage(pageNumber, false);
                        return scroller ;
                    }
                }
            }
        });
    }

    // ************** Event Handlers ****************

    private void loadFile(InputStream is) {
//        final File file = fileChooser.showOpenDialog(pagination.getScene().getWindow());
        if (is != null) {
            final Task<PDFFile> loadFileTask = new Task<PDFFile>() {
                @Override
                protected PDFFile call() throws Exception {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int read = 0;
                    while ((read = is.read(buf, 0, buf.length)) != -1) {
                        baos.write(buf, 0, read);
                    }
                    baos.flush();
                    byte[] bytes = baos.toByteArray();

                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                        return new PDFFile(buffer);
                }
            };
            loadFileTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    pagination.getScene().getRoot().setDisable(false);
                    final PDFFile pdfFile = loadFileTask.getValue();
                    currentFile.set(pdfFile);
                }
            });
            loadFileTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    pagination.getScene().getRoot().setDisable(false);
                    showErrorMessage("Could not load file "+ file_manual, loadFileTask.getException());
                }
            });
            //pagination.getScene().getRoot().setDisable(true);
            imageLoadService.submit(loadFileTask);
        }
    }

    @FXML private void zoomIn() {
        zoom.set(zoom.get()*ZOOM_DELTA);
    }

    @FXML private void zoomOut() {
        zoom.set(zoom.get()/ZOOM_DELTA);
    }

    @FXML private void zoomFit() {
        // the -20 is a kludge to account for the width of the scrollbars, if showing.
        double horizZoom = (scroller.getWidth()-20) / currentPageDimensions.width ;
        double verticalZoom = (scroller.getHeight()-20) / currentPageDimensions.height ;
        zoom.set(Math.min(horizZoom, verticalZoom));
    }

    @FXML private void zoomWidth() {
        zoom.set((scroller.getWidth()-20) / currentPageDimensions.width) ;
    }

    // *************** Background image loading ****************

    private void updateImage(final int pageNumber, boolean fromZoom) {
        final Task<ImageView> updateImageTask = new Task<ImageView>() {
            @Override
            protected ImageView call() throws Exception {
                PDFPage page = currentFile.get().getPage(pageNumber+1);
                Rectangle2D bbox = page.getBBox();
                final double actualPageWidth = bbox.getWidth();
                final double actualPageHeight = bbox.getHeight();
                // record page dimensions for zoomToFit and zoomToWidth:
                currentPageDimensions = new PageDimensions(actualPageWidth, actualPageHeight);
                // width and height of image:
                final int width = (int) (actualPageWidth * zoom.get());
                final int height = (int) (actualPageHeight * zoom.get());
                // retrieve image for page:
                // width, height, clip, imageObserver, paintBackground, waitUntilLoaded:
                java.awt.Image awtImage = page.getImage(width, height, bbox, null, true, true);
                // draw image to buffered image:
                BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                buffImage.createGraphics().drawImage(awtImage, 0, 0, null);
                // convert to JavaFX image:
                Image image = SwingFXUtils.toFXImage(buffImage, null);
                // wrap in image view and return:
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                return imageView ;
            }
        };

        updateImageTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                pagination.getScene().getRoot().setDisable(false);
                ImageView imageView = updateImageTask.getValue();

                currentImage.set(imageView);
//                scroller.setPrefSize(updateImageTask.getValue().getImage().getWidth(), updateImageTask.getValue().getImage().getHeight());
//                scroller.setMinSize(updateImageTask.getValue().getImage().getWidth(), updateImageTask.getValue().getImage().getHeight());
                scroller.setPrefSize(700d, 500d);
                scroller.setMinSize(700d, 500d);
                if (!fromZoom) {
//                    btn_zoom_fit.fire();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    btn_zoom_fit.fire();
                }
            }
        });

        updateImageTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                pagination.getScene().getRoot().setDisable(false);
                updateImageTask.getException().printStackTrace();
            }

        });

        pagination.getScene().getRoot().setDisable(true);
        imageLoadService.submit(updateImageTask);
    }

    private void showErrorMessage(String message, Throwable exception) {

        //  move to fxml (or better, use ControlsFX)

        final Stage dialog = new Stage();
        dialog.initOwner(pagination.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        final VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        StringWriter errorMessage = new StringWriter();
        exception.printStackTrace(new PrintWriter(errorMessage));
        final Label detailsLabel = new Label(errorMessage.toString());
        TitledPane details = new TitledPane();
        details.setText("Details:");
        Label briefMessageLabel = new Label(message);
        final HBox detailsLabelHolder =new HBox();

        Button closeButton = new Button("OK");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.hide();
            }
        });
        HBox closeButtonHolder = new HBox();
        closeButtonHolder.getChildren().add(closeButton);
        closeButtonHolder.setAlignment(Pos.CENTER);
        closeButtonHolder.setPadding(new Insets(5));
        root.getChildren().addAll(briefMessageLabel, details, detailsLabelHolder, closeButtonHolder);
        details.setExpanded(false);
        details.setAnimated(false);

        details.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    detailsLabelHolder.getChildren().add(detailsLabel);
                } else {
                    detailsLabelHolder.getChildren().remove(detailsLabel);
                }
                dialog.sizeToScene();
            }

        });
        final Scene scene = new Scene(root);

        dialog.setScene(scene);
        dialog.show();
    }


	/*
	 * Struct-like class intended to represent the physical dimensions of a page in pixels
	 * (as opposed to the dimensions of the (possibly zoomed) view.
	 * Used to compute zoom factors for zoomToFit and zoomToWidth.
	 *
	 */

    private class PageDimensions {
        private double width ;
        private double height ;
        PageDimensions(double width, double height) {
            this.width = width ;
            this.height = height ;
        }
        @Override
        public String toString() {
            return String.format("[%.1f, %.1f]", width, height);
        }
    }
}
