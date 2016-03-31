/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package squeezeboard.model;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import squeezeboard.SqueezeBoard;
import squeezeboard.SqueezeBoardController;
import squeezeboard.controller.CellEventListner;
import squeezeboard.controller.ai.minimax.OptimalMoveFinder;
import squeezeboard.controller.pattern.SqueezePattern;
import squeezeboard.controller.pattern.SqueezePatternDirection;
import squeezeboard.controller.pattern.SqueezePatternFinder;
import squeezeboard.controller.pattern.SqueezePatternType;
import squeezeboard.model.Animation.AnimatedGif;
import squeezeboard.view.GridPaneView;
import squeezeboard.view.StatusBarView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zhangwei
 */
public class GameUtils {
    
    public static final String file_empty="/images/emptyCell.png";
    public static final String file_orange="/images/orangeButton.png";
    public static final String file_blue="/images/blueButton.png";
    public static final String file_possMove="/images/possMove.png";
    public static final String file_you_win="/images/Fireworks.jpg";
    public static final String file_computer_win="/images/oops.gif";
    public static final String file_draw_game="/images/draw.jpeg";

    public static final String file_manual="/docs/manual.pdf";
    
    
    public static final Image img_empty = new Image(SqueezeBoard.class.getResourceAsStream(file_empty), 50, 50, true, true);
    public static final Image img_orange = new Image(SqueezeBoard.class.getResourceAsStream(file_orange), 50, 50, true, true);
    public static final Image img_blue = new Image(SqueezeBoard.class.getResourceAsStream(file_blue), 50, 50, true, true);
    public static final Image img_possMove = new Image(SqueezeBoard.class.getResourceAsStream(file_possMove), 50, 50, true, true);
    public static final Image img_you_win = new Image(SqueezeBoard.class.getResourceAsStream(file_you_win));
    public static final Image img_draw = new Image(SqueezeBoard.class.getResourceAsStream(file_draw_game));
    //public static final Image img_computer_win = new Image(SqueezeBoard.class.getResourceAsStream(file_computer_win));
    
    public static double effecthreshold = 1.0d;
    
    private static Effect bloom = new Bloom(effecthreshold);
    
    public static final AtomicInteger round = new AtomicInteger(0);
    
    public static final AtomicBoolean game_started = new AtomicBoolean(false);
    
    /**
     * computer is blue originally
     */
    public static PlayerColor computerRole = PlayerColor.blue;
    
    public static PlayerColor currentColor = PlayerColor.orange;
    
    public static AtomicInteger orangeLeft;
    public static AtomicInteger blueLeft;
    
    
    public static CellData pickedCell = null;

    public static final int GRID_DIMENSION = 8;

    public static int MAXIMUM_MOVES = 100;

    public static int SEARCH_WIDTH = 12;
    public static int SEARCH_DEPTH = 3;

    public static final ImageView[][] imgMatrix = new ImageView[GRID_DIMENSION][GRID_DIMENSION];


    public static BoardConfiguration[] existingMoves;
    
    public static final AtomicInteger currentCursor = new AtomicInteger(0);

    public static SqueezeBoardController mainController;

    public static OptimalMoveFinder moveFinder = OptimalMoveFinder.SEQUENTIAL;

    public static void computerAction() {
        if (game_started.get()) {
            Pair<CellData, CellData> optimalMove = null;
            if (GameUtils.computerRole.equals(GameUtils.currentColor)) {
                int leftPieces = getCurrentBoardConfiguration().getNumberOfPieces(computerRole);
                if (getCurrentBoardConfiguration().getNumberOfPieces(computerRole) > 4) {
                    SEARCH_WIDTH = 20 - leftPieces;
                } else {
                    //TODO: TEST IF THE SEARCH_WIDTH SHOULD VARY FROM TIME TO TIME.
                    SEARCH_WIDTH = 20 - leftPieces;
                }
                try {
                    optimalMove = moveFinder.findOptimalMove(GameUtils.computerRole, getCurrentBoardConfiguration());
                    System.out.println(optimalMove.toString());
                    if (optimalMove != null) {
                        GameUtils.computerMoveByEvent(optimalMove);
                    } else {
                        Pair<Integer, Integer> left = GameUtils.getComputerPlayerLeft();
                        GameUtils.game_over(left.getFirst(), left.getSecond(), null);
                    }
                } catch (Throwable t) {
                    System.out.println(String.format("%s : %s", t.getClass(), t.getLocalizedMessage()));
                    Pair<Integer, Integer> left = GameUtils.getComputerPlayerLeft();
                    GameUtils.game_over(left.getFirst(), left.getSecond(), t);
                }
            }
        }
    }

    public static Node getNodeByRowColumnIndex(final int row,final int column,GridPane gridPane) {
        Node result = null;
        Integer r = new Integer(row);
        Integer c = new Integer(column);
        ObservableList<Node> childrens = gridPane.getChildren();
        for(Node node : childrens) {
            if(r.equals(gridPane.getRowIndex(node)) && c.equals(gridPane.getColumnIndex(node))) {
                result = node;
                break;
            }
        }
        return result;
    }
    
    public static void renderGridView(BoardConfiguration boardConfiguration, GridPane gridView, 
            int dimension, GridPaneView gridController, StatusBarView statusBarView) {
        ImageView imgView;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                CellData cell = boardConfiguration.getBoard()[i][j];
                if (gridController != null) {
                    imgView = new ImageView();
                    imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, new CellEventListner(gridController, statusBarView));
                    gridView.add(imgView, j, i);
                    imgMatrix[i][j] = imgView;
                } else {
                    imgView = (ImageView) getNodeByRowColumnIndex(i, j, gridView);
                }
                imgView.setUserData(cell);
                setPictureToImageView(cell, imgView);
            }
        }
    }

    public static void checkAndHighlight(CellData currCell, CellData[][] grid,
                                         int rowIncr, int colIncr, List<CellData> possMoves){
        int ir = rowIncr;
        int ic = colIncr;
        int boundReached = 0;
        int row = currCell.getRowCord();
        int col = currCell.getColCord();
        int newcol = col + ic;
        int newrow = row + ir;
        while (true) {
            if (newcol <= 7 && newrow <=7
                    && newcol >= 0 && newrow >= 0 && grid[newrow][newcol].getCellChar()=='E') {
                if (possMoves != null) {
                    possMoves.add(grid[newrow][newcol]);
                }
                grid[newrow][newcol].setCellChar('P');
                newcol = newcol + ic;
                newrow = newrow + ir;
            } else {
                ir = -ir;
                ic = -ic;
                newcol = col + ic;
                newrow = row + ir;
                boundReached++;
            }
            if (boundReached >= 2) {
                break;
            }
        }
    }

    public static void removeHighlight(CellData[][] grid) {
        int d = grid.length;
        for (int i = 0; i < d ; i++) {
            for (int j = 0; j< d; j++) {
                if(grid[i][j].getCellChar() =='P'){
                    grid[i][j].setCellChar('E');
                }
            }
        }
    }

    private static void setPictureToImageView(CellData cell, ImageView imgView) {
        char cellChar = cell.getCellChar();
        Effect effect = null;
        Image img = imgView.getImage();
        switch (cellChar) {
            case 'P':
                effect = bloom;
                img = GameUtils.img_possMove;
                break;
            case 'E':
                img = GameUtils.img_empty;
                effect = null;
                break;
            case 'O':
                img = GameUtils.img_orange;
                effect = null;
                break;
            case 'B':
                img = GameUtils.img_blue;
                effect = null;
                break;
            default:
                ;
        }
        
        imgView.setImage(img);
        imgView.setEffect(effect);
        if (cell.equals(GameUtils.pickedCell)) {
            imgView.setEffect(bloom);
        }
    }
    
    public static BoardConfiguration getCurrentBoardConfiguration() {
        if (currentCursor.get() >= existingMoves.length) {
            return existingMoves[existingMoves.length - 1];
        }
        return existingMoves[currentCursor.get()];
    }
    
    public static CellData[][] getCurrentBoard(){
        return getCurrentBoardConfiguration().getBoard();
    }

    /**
     * after dropping a piece on the board, the board is cloned.
     * @param currentColor  current player color who dropped piece on the board.
     * @return
     */
    public static BoardConfiguration copyCurrentConfiguration(PlayerColor currentColor) {
        if (currentColor == null) {
            throw new IllegalStateException("current color should not be null!");
        }
        //existingMoves[currentCursor.get()].setMoveMaker(currentColor);
        BoardConfiguration clonedBoard = existingMoves[currentCursor.get()].clone();
        clonedBoard.setMoveMaker(currentColor);
        existingMoves[currentCursor.incrementAndGet()] = clonedBoard;
        return clonedBoard;
    }
    
    public static BoardConfiguration undoConfiguration() {
        BoardConfiguration currentConfig = existingMoves[currentCursor.get()];
        if (currentCursor.get() > 0) {
            currentConfig.destroy();
            currentConfig = existingMoves[currentCursor.decrementAndGet()];
            currentColor = currentConfig.getMoveMaker().getOpponentColor();
            Pair<Integer, Integer> blue_orange_count = calculateLeftPiecesCount(currentConfig);
            blueLeft.set(blue_orange_count.getFirst());
            orangeLeft.set(blue_orange_count.getSecond());
        }
        return currentConfig;
    }
    
    
    public static void showAlertBox(PromptableException.ExceptFactor exceptFactor) {
        Alert alert = new Alert(exceptFactor.getAlertType().equals(Alert.AlertType.NONE)?
                Alert.AlertType.INFORMATION:exceptFactor.getAlertType(), exceptFactor.getMsg());
        alert.setTitle(exceptFactor.getTitleText());
        alert.setHeaderText(exceptFactor.getHeaderText());
        if(exceptFactor.getAlertType().equals(Alert.AlertType.NONE)){
            if (exceptFactor.equals(PromptableException.ExceptFactor.YOU_WIN)) {
                alert.setGraphic(new ImageView(img_you_win));
            } else if (exceptFactor.equals(PromptableException.ExceptFactor.COMPUTER_WIN)){
                AnimatedGif img_computer_win = new AnimatedGif(
                        SqueezeBoard.class.getResource(file_computer_win).toExternalForm(), 2200);
                img_computer_win.setCycleCount(200);
                img_computer_win.play();
                alert.setGraphic(img_computer_win.getView());
            } else if (exceptFactor.equals(PromptableException.ExceptFactor.DRAW_GAME)) {
                alert.setGraphic(new ImageView(img_draw));
            }
        }
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> System.out.println(response.getButtonData()));
    }

    /**
     *
     * @param computerLeft
     * @param playerLeft
     * @return whether there is someone winning this game
     */
    public static PromptableException.ExceptFactor determineGameResult(int moveCounter, int computerLeft, int playerLeft){
        if (moveCounter >= GameUtils.MAXIMUM_MOVES * 2) {
            // GAME MUST COME TO AN END HERE, which one has the most pieces on the board will win
            return getDifferentGameResult(computerLeft,playerLeft);
        } else {
            // if anyone has only one piece left on the board, he will lost.
            if (computerLeft <= 0 || playerLeft <= 0) {
                return getDifferentGameResult(computerLeft,playerLeft);
            } else if (computerLeft == 1 && playerLeft == 1) {
                return getDifferentGameResult(computerLeft, playerLeft);
            }
        }
        return null;
    }

    private static PromptableException.ExceptFactor getDifferentGameResult(int computerLeft, int playerLeft){
        if (computerLeft > playerLeft) {
            return PromptableException.ExceptFactor.COMPUTER_WIN;
        } else if (playerLeft > computerLeft) {
            return PromptableException.ExceptFactor.YOU_WIN;
        } else if (computerLeft == playerLeft) {
            return PromptableException.ExceptFactor.DRAW_GAME;
        }
        return null;
    }

    public static int tryRemovePattern(CellData cell, BoardConfiguration boardConfiguration, PlayerColor color) {
        Map<SqueezePatternType, List<SqueezePattern>> pattern =
                SqueezePatternFinder.findPattern(boardConfiguration, cell, color);
        List<SqueezePattern> squeezePatterns = pattern.get(SqueezePatternType.FULFILLED_GAP);
        Optional<SqueezePattern> patternToRemove = squeezePatterns.stream()
                .max((f, s) -> Integer.compare(f.validRemovalCount(), s.validRemovalCount()));
        if (patternToRemove.isPresent()) {
            return patternToRemove.get().tryEliminate(cell, boardConfiguration.getBoard());
        }
        return 0;
    }

    public static int findDangerForPlayer(CellData cell, BoardConfiguration boardConfiguration, PlayerColor color) {
        Map<SqueezePatternType, List<SqueezePattern>> pattern =
                SqueezePatternFinder.findPattern(boardConfiguration, cell, color.getOpponentColor());
        List<SqueezePattern> patterns = pattern.get(SqueezePatternType.GAP);
        patterns.addAll(pattern.get(SqueezePatternType.INCOMPLETE_GAP));

        Optional<Pair<SqueezePattern, Integer>> maxDanger = patterns.stream().map(pt -> {
            int c = cell.getColCord();
            int r = cell.getRowCord();
            List<Tuple<CellData, CellData, Integer>> allSupportivePieces =
                    SqueezePatternDirection.findAllSupportivePieces(0, c, r, color.getOpponentColor(), boardConfiguration.getBoard()
                            , false);
            boolean inDanger = allSupportivePieces.stream().filter(item -> {
                int horz_dist = Math.abs(SqueezePatternDirection.HORIZONTAL.getIndexInAGroup(item.getFirst())
                        - pt.getSqueezePatternDirection().getIndexInAGroup(item.getSecond()));
                int vert_dist = Math.abs(SqueezePatternDirection.VERTICAL.getIndexInAGroup(item.getFirst())
                        - pt.getSqueezePatternDirection().getIndexInAGroup(item.getSecond()));
                return (horz_dist > 0 || vert_dist > 0);
            }).findAny().isPresent();
            return new Pair<>(pt, inDanger ? pt.validRemovalCount() : 0);
        }).max((a, b) -> Integer.compare(a.getSecond(), b.getSecond()));

        return (maxDanger.isPresent()? maxDanger.get().getSecond() : 0);
    }

    public static void computerMoveByEvent(Pair<CellData, CellData> move) {
        //Pick piece
        fireEventOnCellImgView(move.getFirst());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {

        }
        //drop piece
        fireEventOnCellImgView(move.getSecond());
    }

    public static void fireEventOnCellImgView(CellData cord) {
        ImageView imgView = imgMatrix[cord.getRowCord()][cord.getColCord()];
        Event.fireEvent(imgView, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }


    public static Pair<Integer, Integer> getComputerPlayerLeft() {
        int playerLeft = GameUtils.orangeLeft.get();
        int computerLeft = GameUtils.blueLeft.get();
        if (GameUtils.computerRole.equals(PlayerColor.orange)) {
            playerLeft = GameUtils.blueLeft.get();
            computerLeft = GameUtils.orangeLeft.get();
        }
        return new Pair<>(computerLeft, playerLeft);
    }

    public static void game_over(int computerLeft, int playerLeft, Throwable t) {
        if (mainController.getBtn_start().isSelected()) {
            int moveCounter = t!=null? Integer.MAX_VALUE:GameUtils.currentCursor.get();
            PromptableException.ExceptFactor gameResult = GameUtils.determineGameResult(moveCounter,
                    computerLeft, playerLeft);
            if (gameResult != null) {
                GameUtils.showAlertBox(gameResult);
                mainController.getBtn_start().fire();
            }
        }
    }

    public static Pair<Integer,Integer> calculateLeftPiecesCount(BoardConfiguration newboard) {
        int orangeLeft = 0;
        int blueLeft = 0;
        CellData[][] boardData = newboard.getBoard();
        for (CellData[] row : boardData) {
            for (CellData cell : row) {
                if (cell.getCellChar() == PlayerColor.blue.CHAR()) {
                    blueLeft ++;
                } else if (cell.getCellChar() == PlayerColor.orange.CHAR()) {
                    orangeLeft ++;
                }
            }
        }
        return new Pair<>(blueLeft, orangeLeft);
    }
}
