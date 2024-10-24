package scenes.scene.game;

import function.calc.CalcUtil;
import scenes.scene.game.gameobject.*;
import sceneutil.SceneDrawer;
import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;
import sceneutil.frametimer.FrameTimer;

import java.awt.*;
import java.util.List;

public class GameDrawer extends SceneDrawer {
    // 背景
    public void drawBackground(Graphics2D g2d) {
        backgroundBp.fill(g2d);
        gridFieldBackBp.fill(g2d);
    }

    // 通常壁
    public void drawNormalWall(Graphics2D g2d, NormalWall floor, NormalWall roof) {
        drawWall(g2d, floor);
        drawWall(g2d, roof);
    }

    // 電気壁
    public void drawElectricWall(Graphics2D g2d, ElectricWall wallLeft, ElectricWall wallRight) {
        drawWall(g2d, wallLeft);
        drawWall(g2d, wallRight);
    }
    // ゲームオブジェクトの描画
    private void drawWall(Graphics2D g2d, GameObject go) {
        List<Blueprint> bps = go.getBlueprint();
        for (Blueprint bp : bps) {
            bp.fill(g2d);
        }
    }

    // ガイド
    public void drawGuide(Graphics2D g2d, int x, int y, GameScene.State gamestate, Block controllable) {
        if(gamestate == GameScene.State.PLAYING) {
            guideBp.setAnchorPoint(150 + x * 50, 75 + y * 50);

            Point position = new Point(x, y);
            Block copy = new Block(controllable);
            copy.setPlace(position);
            List<Blueprint> copyBps = copy.getBlueprint();
            for(Blueprint bp : copyBps) {
                bp.fill(g2d);
            }
            guideBp.fill(g2d);
        }
    }

    // ブロック
    public void drawBlocks(Graphics2D g2d, List<Block> blocks, GameScene.State gameState) {
        if(gameState == GameScene.State.PLAYING || gameState == GameScene.State.GAME_OVER) {
            for (Block block : blocks) {
                drawBlock(g2d, block);
            }
        }
    }
    public void drawBlock(Graphics2D g2d, Block block) {
        List<Blueprint> bps = block.getBlueprint();
        for (Blueprint bp : bps) {
            bp.fill(g2d);
        }
        bps.get(0).draw(g2d, Color.WHITE); // 外殻に縁線を描く

        // デバッグ用
        List<FontBox> fbs = block.getFontBox(g2d);
        //fbs.get(0).drawWithSetColor(g2d);
    }


    // スコアボード
    public void drawScoreBoard(Graphics2D g2d, int score, int hiScore, int blockCount, int connectCount) {
        scoreBoardBp.fill(g2d);

        scoreLFb.drawWithSetColor(g2d);
        hiScoreLFb.drawWithSetColor(g2d);
        blocksLFb.drawWithSetColor(g2d);
        connectLFb.drawWithSetColor(g2d);

        scoreRFb.setStr(calc.paddingZero(score, 5) );
        scoreRFb.setPoint(new Point(780 - scoreRFb.strWidth(g2d), 235));
        scoreRFb.drawWithSetColor(g2d);
        hiScoreRFb.setStr(calc.paddingZero(hiScore, 5) );
        hiScoreRFb.setPoint(new Point(780 - hiScoreRFb.strWidth(g2d), 270));
        hiScoreRFb.drawWithSetColor(g2d);
        blocksRFb.setStr(calc.paddingZero(blockCount, 3) );
        blocksRFb.setPoint(new Point(780 - blocksRFb.strWidth(g2d), 335));
        blocksRFb.drawWithSetColor(g2d);
        connectRFb.setStr(calc.paddingZero(connectCount, 3) );
        connectRFb.setPoint(new Point(780 - connectRFb.strWidth(g2d), 370));
        connectRFb.drawWithSetColor(g2d);
    }

    // 次のブロック
    public void drawNextBlock(Graphics2D g2d, Block nextBlock, Point point, GameScene.State gameState) {
        nextBlockFrameBp.setAnchorPoint(point);
        nextBlockFrameBp.fill(g2d);
        nextBlockBackBp.setAnchorPoint(point);
        nextBlockBackBp.fill(g2d);

        if(nextBlock != null && gameState == GameScene.State.PLAYING) {
            List<Blueprint> bps = nextBlock.getBlueprint();
            for (Blueprint bp : bps) {
                bp.fill(g2d);
            }
            bps.get(0).draw(g2d, Color.WHITE); // 外殻に縁線を描く
        }

        nextFb.drawWithSetColor(g2d);
    }

    // 仮想コントローラ
    public void drawVirtualController(Graphics2D g2d, VirtualController vController, boolean isVisible) {
        if(isVisible) {
            List<Blueprint> bps = vController.getBlueprint();
            for (Blueprint bp : bps) {
                bp.fill(g2d);
            }
        }
    }

    // 薄い暗幕（ゲームオーバーと一時停止時）
    public void drawCurtain(Graphics2D g2d, GameScene.State gameState, Point nextBlockPoint, boolean isReady, FrameTimer gameOverMotionTimer) {
        if(gameState == GameScene.State.READY && !isReady
                || gameState == GameScene.State.GAME_OVER
                || gameState == GameScene.State.PAUSE) {
            curtainBp.fill(g2d);
            curtainNextBlockBp.setAnchorPoint(nextBlockPoint);
            curtainNextBlockBp.fill(g2d);

            int time = gameOverMotionTimer.getTimer();
            gameOverMotionBp.setColor(
                    time % 10 > 5 && time > 10
                            ? gameOverMotionOnColor
                            : gameOverMotionOffColor
            );
            gameOverMotionBp.fill(g2d);

        }
    }

    // デカ文字と案内文字
    public void drawLiteral(Graphics2D g2d, GameScene.State gameState, boolean isReady, boolean isZeroGameOverTimer) {
        if(gameState == GameScene.State.READY) {
            if(isReady) {
                drawLargeLiteral(g2d, readyFb);
            }
            else {
                drawSmallLiteral(g2d, pressEnterStartFb);
            }
        }
        else if(gameState == GameScene.State.GAME_OVER && isZeroGameOverTimer) {
            drawLargeLiteral(g2d, gameOverFb);
            drawSmallLiteral(g2d, pressEnterRestartFb);
        }
        else if(gameState == GameScene.State.PAUSE) {
            drawLargeLiteral(g2d, pauseFb);
            drawSmallLiteral(g2d, pressPUnpauseFb);
        }
    }
    public void drawLargeLiteral(Graphics2D g2d, FontBox fb) {
        int strWidth  = fb.strWidth(g2d);
        int strHeight = fb.strHeight(g2d);
        fb.setPoint(new Point(275 - strWidth / 2, 300 + strHeight / 5));
        fb.drawOneTimeParam(g2d, Color.GRAY, 2, 2);
        fb.drawWithSetColor(g2d);
    }
    public void  drawSmallLiteral(Graphics2D g2d, FontBox fb) {
        int strWidth  = fb.strWidth(g2d);
        int strHeight = fb.strHeight(g2d);
        fb.setPoint(new Point(275 - strWidth / 2, 360 + strHeight / 5));
        fb.drawWithSetColor(g2d);
    }

    @Override
    protected void setBlueprint() {
        backgroundBp.setArea(windowSize.width, windowSize.height);
        backgroundBp.setDrawRectangleMode();

        gridFieldBackBp.setDrawRectangleMode();

        guideBp.setDrawRectangleMode();

        scoreBoardBp.setDrawRectangleMode();

        nextBlockFrameBp.setDrawRectangleMode();
        nextBlockBackBp.setDrawRectangleMode();

        curtainBp.setDrawRectangleMode();
        curtainNextBlockBp.setDrawRectangleMode();

        gameOverMotionBp.setDrawRectangleMode();
    }

    @Override
    protected void setAnimationTimer(int frameRate) { }
    @Override
    protected void pastAnimationTimer() { }

    // --------------------------------------------------- //

    CalcUtil calc = new CalcUtil();

    ParamDrawing backgroundParam = new ParamDrawing(0, 0, null, null);
    Color backgroundColor = Color.DARK_GRAY;
    Blueprint backgroundBp = new Blueprint(backgroundParam, backgroundColor);

    ParamDrawing gridFieldBackParam = new ParamDrawing(125, 50, 300, 500);
    Color gridFieldBackColor = Color.BLACK;
    Blueprint gridFieldBackBp = new Blueprint(gridFieldBackParam, gridFieldBackColor);

    ParamDrawing guideParam = new ParamDrawing(null, null, 50, 50);
    SideDrawing guideSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    Color guideColor = new Color(0, 0, 0, 220);
    Blueprint guideBp = new Blueprint(guideParam, guideSide, guideColor);

    ParamDrawing scoreBoardParam = new ParamDrawing(550, 0, 250, 600);
    Blueprint scoreBoardBp = new Blueprint(scoreBoardParam, Color.BLACK);

    SideDrawing nextBlockSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    ParamDrawing nextBlockFrameParam = new ParamDrawing(null, null, 50);
    Color nextBlockFrameColor = Color.LIGHT_GRAY;
    Blueprint nextBlockFrameBp = new Blueprint(nextBlockFrameParam, nextBlockSide, nextBlockFrameColor);
    ParamDrawing nextBlockBackParam = new ParamDrawing(null, null, 44);
    Color nextBlockBackColor = Color.BLACK;
    Blueprint nextBlockBackBp = new Blueprint(nextBlockBackParam, nextBlockSide, nextBlockBackColor);

    Color curtainColor = new Color(0, 0, 0, 180);
    ParamDrawing curtainParam = new ParamDrawing(0, 0, 550, 600);
    Blueprint curtainBp = new Blueprint(curtainParam, curtainColor);
    ParamDrawing curtainNextBlockParam = new ParamDrawing(null, null, 44);
    SideDrawing curtainNextBlockSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    Blueprint curtainNextBlockBp = new Blueprint(curtainNextBlockParam, curtainNextBlockSide, curtainColor);

    ParamDrawing gameOverMotionParam = new ParamDrawing(125, 50, 300, 500);
    Color gameOverMotionOnColor = new Color(200, 0, 0, 50);
    Color gameOverMotionOffColor = new Color(200, 0, 0, 0);
    Blueprint gameOverMotionBp = new Blueprint(gameOverMotionParam, gameOverMotionOffColor);

    Font largeFont = FontBox.Arial(72, FontBox.BOLD);
    FontBox readyFb = new FontBox("Ready?", largeFont, Color.WHITE, null);
    FontBox pauseFb = new FontBox("Pause", largeFont, Color.WHITE, null);
    FontBox gameOverFb = new FontBox("Game Over", largeFont, Color.WHITE, null);

    Font smallFont = FontBox.Arial(20, FontBox.BOLD);
    FontBox pressEnterStartFb = new FontBox("Press ENTER Start", smallFont, Color.WHITE, null);
    FontBox pressEnterRestartFb = new FontBox("Press Enter Restart", smallFont, Color.WHITE, null);
    FontBox pressPUnpauseFb = new FontBox("Press P Unpause", smallFont, Color.WHITE, null);

    Font scoreFont = FontBox.MSGothic(25, FontBox.BOLD);
    FontBox scoreLFb = new FontBox("SCORE:", scoreFont, Color.WHITE, new Point(570, 235));
    FontBox hiScoreLFb = new FontBox("HI-SCORE:", scoreFont, Color.WHITE, new Point(570, 270));
    FontBox blocksLFb = new FontBox("BLOCKS:", scoreFont, Color.WHITE, new Point(570, 335));
    FontBox connectLFb = new FontBox("CONNECT:", scoreFont, Color.WHITE, new Point(570, 370));

    FontBox scoreRFb = new FontBox("", scoreFont, Color.WHITE, null);
    FontBox hiScoreRFb = new FontBox("", scoreFont, Color.WHITE, null);
    FontBox blocksRFb = new FontBox("", scoreFont, Color.WHITE, null);
    FontBox connectRFb = new FontBox("", scoreFont, Color.WHITE, null);

    Font nextFont = FontBox.MSGothic(27, FontBox.BOLD);
    FontBox nextFb = new FontBox("NEXT", nextFont, Color.WHITE, new Point(650, 60));
}
