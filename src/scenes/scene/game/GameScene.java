package scenes.scene.game;

import function.filemanage.bgm.BackGroundMusicManager;
import function.filemanage.se.SoundEffectManager;
import function.logger.MessageLogger;
import scenes.scene.Scene;
import scenes.scene.game.gameobject.*;
import sceneutil.data.GameDataElements;
import sceneutil.data.GameDataIO;
import sceneutil.SceneBase;
import sceneutil.frametimer.FrameTimer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class GameScene extends SceneBase {
    //コンストラクタ
    public GameScene(GameDataIO dataIO) {
        // 画面サイズ、FPS、キーアサインの初期化
        dataIO.put(GameDataElements.SCENE, Scene.GAME);
        init(dataIO, KEY_ASSIGN, MOUSE_ASSIGN);

        Dimension windowSize = data.getWindowSize();

        drawer.setWindowSize(windowSize);
        drawer.setBlueprint();

        canCheat = data.get(GameDataElements.CHEAT, Boolean.class);

        // 各種ゲームオブジェクト
        // 壁
        circuitMap = new List[6]; // 怪しい 未チェックのキャストって言われる
        for(int i = 0; i < 6; i++) {
            circuitMap[i] = new ArrayList<>();
        }
        circuitTree = new HashMap<>();
        routeConnect = new ArrayList<>();
        wallFloor = new NormalWall( 55,  30);
        wallRoof  = new NormalWall( 55, 550);
        wallLeft  = new ElectricWall( 55,  50, ElectricWall.LEFT);
        wallRight = new ElectricWall(425,  50, ElectricWall.RIGHT);
        vController = new VirtualController(670, 500);

        // 変数
        gameState = State.READY;
        isReady = false;
        blockPos = new Point(INITIAL_POS_X, INITIAL_POS_Y); // 左上を基準とした[X, Y]
        distanceY = DISTANCE_Y_MAX + 1; // 最初だけ1マス下に出てきちゃうから1マス離しておく
        nextPatternNum = Block.getRandomPattern();
        isDeleting = false;
        isPlayingGameOverBGM = false;
        score = 0;
        hiScore = 0;
        isVControllerVisible = true;
        isMute = false;
        pauseBGMPosition = -1;

        // タイマー
        readyTimer.setZero();
        fallTimer.reset();
        pauseCoolTimer.setZero();
        deleteBlockTimer.setZero();

        moveBlock(0);

        /*--------- 処理が重いやつはこの下 ---------*/

        float masterVolume = data.getMasterVolume();
        // 使用SEの読み込み
        seMoveBlock   = data.get(GameDataElements.FILE_SE_MOVE_BLOCK, String.class);
        seDeleteBlock = data.get(GameDataElements.FILE_SE_DELETE_BLOCK, String.class);
        seManager = data.get(GameDataElements.SE_MANAGER, SoundEffectManager.class);
        seManager.loadWaveFile();
        seManager.setMasterVolume(masterVolume); // 主音量を設定
        // 使用BGMの読み込み
        bgmGameStart = data.get(GameDataElements.FILE_BGM_GAME_START, String.class);
        bgmGameOver  = data.get(GameDataElements.FILE_BGM_GAME_OVER, String.class);
        bgmFailed    = data.get(GameDataElements.FILE_BGM_FAILED, String.class);
        bgmPause     = data.get(GameDataElements.FILE_BGM_PAUSE, String.class);
        bgm01        = data.get(GameDataElements.FILE_BGM_BGM01, String.class);
        bgmManager = data.get(GameDataElements.BGM_MANAGER, BackGroundMusicManager.class);
        bgmManager.loadWaveFile();
        bgmManager.setMasterVolume(masterVolume); // 主音量を設定
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {
        drawer.drawBackground(g2d); // 背景
        drawer.drawNormalWall(g2d, wallFloor, wallRoof); // 地面と天井
        drawer.drawElectricWall(g2d, wallLeft, wallRight); // 電気壁
        drawer.drawGuide(g2d, blockPos.x, DISTANCE_Y_MAX - circuitMap[blockPos.x].size(), gameState, controllableBlock); // ガイド
        drawer.drawBlocks(g2d, blocks, gameState); // ブロック
        drawer.drawScoreBoard(g2d, score, hiScore, Block.getCounter(), connectCount); // 点数表示板
        drawer.drawNextBlock(g2d, nextBlock, nextBlockPos, gameState); // 次のブロック表示
        drawer.drawVirtualController(g2d, vController, isVControllerVisible); // 仮想コントローラ
        drawer.drawCurtain(g2d, gameState, nextBlockPos, isReady, gameOverMotionTimer); // 薄い暗幕とゲーム失敗演出
        drawer.drawLiteral(g2d, gameState, isReady, gameOverMotionTimer.isZero()); // デカ文字と案内文字
    }

    // 毎フレーム処理したい内容はここ
    @Override
    protected void actionField() {
        // デバッグ用
        debugOnKeyPress(
                key.getKeyPress(KeyEvent.VK_D),
                key.getKeyPress(KeyEvent.VK_R),
                key.getKeyPress(KeyEvent.VK_A),
                key.getKeyPress(KeyEvent.VK_V),
                key.getKeyPress(KeyEvent.VK_M)
        );
        boolean isPressedEnter = key.getKeyPress(KeyEvent.VK_ENTER);

        boolean isPressedLeft  = key.getKeyPress(KeyEvent.VK_LEFT);
        boolean isPressedRight = key.getKeyPress(KeyEvent.VK_RIGHT);
        boolean isPressedDown  = key.getKeyPress(KeyEvent.VK_DOWN);
        boolean isPressedUp    = key.getKeyPress(KeyEvent.VK_UP);
        boolean isPressedP     = key.getKeyPress(KeyEvent.VK_P);

        boolean isHeldLeft  = key.getKeyHold(KeyEvent.VK_LEFT);
        boolean isHeldDown  = key.getKeyHold(KeyEvent.VK_DOWN);
        boolean isHeldRight = key.getKeyHold(KeyEvent.VK_RIGHT);
        boolean isHeldUp    = key.getKeyHold(KeyEvent.VK_UP);

        switch (gameState) {
            case READY:
                if( !isReady ) {
                    if(isPressedEnter) {
                        readyTimer.reset();
                        // BGMの再生（ゲームスタート）
                        bgmManager.startSound(bgmGameStart);
                        isReady = true;
                    }
                }
                else if(readyTimer.isZero() || isPressedEnter) {
                    // 最初のブロックを生成
                    controllableBlock = new Block(0, 0, nextPatternNum, true);
                    blocks.add(controllableBlock);
                    moveBlock(0);
                    // Nextのブロックを生成
                    nextPatternNum = Block.getRandomPattern();
                    nextBlock = new Block(nextBlockPos.x, nextBlockPos.y, nextPatternNum, true);
                    // BGMの再生（BGM01）
                    bgmManager.startSound(bgm01);
                    // ゲームを開始
                    gameState = State.PLAYING;
                }
                else {
                    readyTimer.pass();
                }

                break;
            case PLAYING:
                // ブロックの削除中ではないなら
                if( !isDeleting ) {

                    // ←→キーでブロックの移動
                    if(isPressedLeft && !isPressedRight) {
                        moveBlock(LEFT);
                        seManager.startSound(seMoveBlock); // SEの再生
                    } else if (isPressedRight && !isPressedLeft) {
                        moveBlock(RIGHT);
                        seManager.startSound(seMoveBlock); // SEの再生
                    }
                    // ↓キーでブロックの落下
                    if(isPressedDown && !isPressedUp) {
                        fallBlock();
                        seManager.startSound(seMoveBlock); // SEの再生
                    }
                    // ↑キーでブロックを一番下まで落下（ハードドロップ）
                    else if(isPressedUp && !isPressedDown) {
                        int stack = circuitMap[blockPos.x].size();
                        blockPos.y = DISTANCE_Y_MAX - stack;
                        moveBlock(0); // 一番下まで移動する、というプロセスが必要なのでこれを書く
                        fallBlock();
                        seManager.startSound(seMoveBlock); // SEの再生
                    }
                    // 定期的にブロックを落下させる
                    else if(fallTimer.isZero()) {
                        fallBlock();
                    }

                    fallTimer.pass(); // ブロック落下タイマーを経過

                    // スタックが天井に達したらゲームオーバー
                    int stack = circuitMap[INITIAL_POS_X].size();
                    if(stack >= DISTANCE_Y_MAX + 1) {
                        // BGMの再生（ゲーム失敗）
                        bgmManager.startSound(bgmFailed);
                        // ゲームオーバー状態に移行
                        gameState = State.GAME_OVER;
                    }

                    // ブロック削除モーション時の右壁の発光
                    wallRight.charging(deleteBlockTimer, -1);
                }
                else {
                    // 削除モーション状態における削除モーションなどの処理
                    deleting();

                    int startPin = circuitMap[0].indexOf(routeConnect.isEmpty() ? -1 : routeConnect.get(0));
                    int endPin = routeConnect.isEmpty() ? -1 : circuitMap[circuitMap.length - 1].indexOf(routeConnect.get(routeConnect.size() - 1));
                    wallLeft.charging(deleteBlockTimer, startPin);
                    wallRight.charging(deleteBlockTimer, endPin);
                }

                // 仮想コントローラに入力を反映
                vController.inputButtonPress(isHeldLeft, isHeldDown, isHeldRight, isHeldUp);

                // BGMのループ
                if(bgmManager.getPosition() > BGM01_LOOP_POSITION) {
                    bgmManager.setPosition(bgm01, BGM01_LOOP_POSITION / 2);
                }

                // Pキーで一時停止
                if(isPressedP && pauseCoolTimer.isZero() ) {
                    // BGMの一時停止と一時停止用ジングルの再生
                    pauseBGMPosition = bgmManager.pauseSound();
                    bgmManager.startSound(bgmPause);
                    // 一時停止状態に移行
                    gameState = State.PAUSE;
                }
                pauseCoolTimer.pass(); // 一時停止のクールタイムの経過

                break;
            case CLEAR, GAME_OVER:
                if( gameOverMotionTimer.isZero() ) {
                    if( !isPlayingGameOverBGM ) {
                        // BGMの再生（ゲームオーバー）
                        bgmManager.startSound(bgmGameOver);
                        isPlayingGameOverBGM = true;
                    }

                    if (isPressedEnter) {
                        // 初期化
                        initOnRestart();
                        // BGMの再生（ゲームスタート）
                        bgmManager.startSound(bgmGameStart);

                        // Readyタイマーを設定して再スタート
                        readyTimer.reset();
                        gameState = State.READY;
                    }
                }
                else {
                    gameOverMotionTimer.pass();
                }
                // 仮想コントローラへの入力を遮断
                vController.inputButtonPress(false, false, false, false);

                break;
            case  PAUSE:
                // Pキーで一時停止解除
                if(isPressedP) {
                    pauseCoolTimer.reset(); // 一時停止のクールタイムの時間セット
                    // BGM01を途中終了した地点から再スタート
                    bgmManager.restartSound(bgm01, pauseBGMPosition);
                    // ゲームプレー状態に移行
                    gameState = State.PLAYING;
                }
                break;
        }
    }

    /** ブロックの座標更新とブロックの移動 **/
    private void moveBlock(int dir) {
        // キー操作の移動可能範囲である
        if(dir == LEFT  && blockPos.x > 0 || dir == RIGHT && blockPos.x < 5) {
            // かつ、移動先のスタックが自分の座標より高く積まれていない
            int stackToMoveTo = circuitMap[blockPos.x + dir].size();
            if(stackToMoveTo <= DISTANCE_Y_MAX - blockPos.y) {
                // ブロックの座標を変更
                blockPos.x += dir;
            }
        }
        // ブロックのX座標におけるスタックとの距離を更新し、ブロックを移動する
        int stack = circuitMap[blockPos.x].size();
        distanceY = DISTANCE_Y_MAX - stack - blockPos.y;
        if(controllableBlock != null) { // ぬるぽ防止
            controllableBlock.setPlace(blockPos);
        }
    }

    /** ブロックの落下処理 **/
    private void fallBlock() {
        // ブロックの下方に間が空いてるなら
        if(distanceY > 0) {
            blockPos.y++; // ブロックの落下
        }
        // ブロックが着地したら
        else {
            score += 10 + (controllableBlock.getNum() / 10) * 10;
            circuitMap[blockPos.x].add(controllableBlock); // ブロックをマッピング

            // 削除モーション状態ではないなら
            if( !isDeleting ) {
                // 次のブロックを生成
                controllableBlock = new Block(0, 0, nextPatternNum, false);
                blocks.add(controllableBlock);
                // Nextのブロックを生成
                nextPatternNum = Block.getRandomPattern();
                nextBlock = new Block(nextBlockPos.x, nextBlockPos.y, nextPatternNum, true);
                // 仮想位置の指定
                blockPos.x = INITIAL_POS_X;
                blockPos.y = INITIAL_POS_Y;
            }
            makeCircuitTree(); // 回路の接続ツリーを作成
            List<Block> route = makeBlockRoute(); // ルートを作成

            // 左端から右端まで繋がっているなら
            if ( checkRouteConnect(route) ) {
                // 削除タイマーを稼働し、削除モーション状態にする
                routeConnect = new ArrayList<>(route);
                deleteBlockTimer.reset();
                isDeleting = true;
                int routeSize = routeConnect.size();
                routeConnect.get(0).energize(Block.LEFT);
                routeConnect.get(routeSize - 1).energize(Block.RIGHT);
                for(int i = 0; i < routeSize - 1; i++) {
                    Block fromBlock = routeConnect.get(i);
                    Block toBlock = routeConnect.get(i + 1);
                    fromBlock.unEnergized(); // 非通電部分を石化
                    energizeCircuit(fromBlock, toBlock);
                }
                routeConnect.get(routeSize - 1).unEnergized();
                seManager.startSound(seDeleteBlock); // SEの再生
                //printDataDump(); // デバッグ用
            }
        }
        moveBlock(0); // ブロックの座標更新とブロックの移動

        fallTimer.reset();
    }
    /** 指定されたブロックの位置関係から適切な回路を点灯させる **/
    private void energizeCircuit(Block fromBlock, Block toBlock) {
        int fromPosX = getPosX(fromBlock);
        int fromPosY = getPosY(fromBlock);
        int toPosX = getPosX(toBlock);
        int toPosY = getPosY(toBlock);

        if(fromPosY == toPosY) {
            if(fromPosX < toPosX) {
                fromBlock.energize(Block.RIGHT);
                toBlock.energize(Block.LEFT);
            }
            else if(toPosX < fromPosX) {
                fromBlock.energize(Block.LEFT);
                toBlock.energize(Block.RIGHT);
            }
        }
        else if(fromPosX == toPosX) {
            if(fromPosY < toPosY) {
                fromBlock.energize(Block.TOP);
                toBlock.energize(Block.BOTTOM);
            }
            else { // if(toPosY < fromPosY)
                fromBlock.energize(Block.BOTTOM);
                toBlock.energize(Block.TOP);
            }
        }
    }
    private int getPosX(Block block) {
        int posX = -1;
        for(int i = 0; i < circuitMap.length; i++) {
            if( circuitMap[i].contains(block) ) {
                posX = i;
                break;
            }
        }
        return posX;
    }
    private int getPosY(Block block) {
        int posY = -1;
        for(List<Block> bs : circuitMap) {
            posY = bs.indexOf(block);
            if(posY != -1) {
                break;
            }
        }
        return posY;
    }

    /** 回路の接続ツリーを作成 **/
    private void makeCircuitTree() {
        int x = 0;
        circuitTree.clear();
        for(List<Block> laneBlocks : circuitMap) {
            int y = 0;
            for(Block block : laneBlocks) {
                List<Block> branches = new ArrayList<>();
                boolean[] circuit = block.getCircuit();
                int dir = 0;
                for(boolean b : circuit) {
                    if(b) {
                        Block branch = getBranchBlock(x, y, dir);
                        if (branch != null) {
                            branches.add(branch);
                        }
                    }
                    dir++;
                }
                circuitTree.put(block, branches);
                y++;
            }
            x++;
        }
    }
    /** 接続可能なブロックを取得 **/
    private Block getBranchBlock(int posX, int posY, int dir) {
        List<Block> laneBlocks = circuitMap[posX];

        // ブロックの回路が上方向、かつY位置が上端でない
        if (dir == Block.TOP && posY < laneBlocks.size() - 1) {
            // 同レーンの上方のブロックを取得し、その回路が下に伸びていたら返す
            Block toTop = laneBlocks.get(posY + 1);
            return toTop.getCircuit(Block.BOTTOM) ? toTop : null;
        }
        // ブロックの回路が下方向、かつY位置が下端でない
        else if (dir == Block.BOTTOM && posY > 0) {
            // 同レーンの下方のブロックを取得し、その回路が上に伸びていたら返す
            Block toBottom = laneBlocks.get(posY - 1);
            return toBottom.getCircuit(Block.TOP) ? toBottom : null;
        }
        // ブロックの回路が左方向、かつX位置が左端でない
        else if (dir == Block.LEFT && posX > 0) {
            // 左レーンのブロック数が足りるか確認する
            List<Block> laneBlocksLeft = circuitMap[posX - 1];
            if (posY < laneBlocksLeft.size() ) {
                // 左レーンの同Y位置のブロックを取得し、その回路が右に伸びていたら返す
                Block toLeft = laneBlocksLeft.get(posY);
                return toLeft.getCircuit(Block.RIGHT) ? toLeft : null;
            }
            return null;
        }
        // ブロックの回路が右方向、かつX位置が右端でない
        else if (dir == Block.RIGHT && posX < circuitMap.length - 1) {
            // 右レーンのブロック数が足りるか確認する
            List<Block> laneBlocksRight = circuitMap[posX + 1];
            if (posY < laneBlocksRight.size() ) {
                // 右レーンの同Y位置のブロックを取得し、その回路が右に伸びていたら返す
                Block toRight = laneBlocksRight.get(posY);
                return toRight.getCircuit(Block.LEFT) ? toRight : null;
            }
            return null;
        }
        // 回路がない場合
        return null;
    }

    /** ツリーを探索し、ルートを生成する **/
    private List<Block> makeBlockRoute() {
        List<List<Block>> routes = new ArrayList<>(); // 複数のルート確保用
        List<Block> route = new ArrayList<>();
        List<Block> r = new ArrayList<>();
        boolean hasRoute = false;

        // 左端のブロック列を取っ掛かりとして探索する
        for(Block block : circuitMap[0]) {
            // 左壁との接続があれば探索を開始する
            if( isStartBlock(block) ) {
                route.clear();
                route.add(block);
                // 回路ツリーを探索させ、ルートを構成する
                connectRoute(route);

                // 構成したルートがゴールしていたら
                if( checkRouteConnect(route) ) {
                    if( !hasRoute ) {
                        r = new ArrayList<>(route);
                        hasRoute = true;
                    }
                    routes.add( new ArrayList<>(route) );
                }
            }
        }
        for(List<Block> rt : routes) {
            //printConnectRoute(rt);
        }
        return r;
    }
    /** 再帰処理でツリーを探索 **/
    private void connectRoute(List<Block> route) {
        // 末端のブロックがゴールできない場合
        if ( !checkRouteConnect(route) ) {
            // 回路ツリーから現在のブロックをキーとしてその先の接続候補を取得
            List<Block> branch = circuitTree.get(route.get(route.size() - 1));

            //printBranch("1", route, branch); // デバッグ用
            for (Block b : branch) {
                // 接続候補のうち1つが、既に通ったブロックでなければ
                if( !route.contains(b) ) {
                    route.add(b); // ルートに接続候補のひとつを追加
                    //printBranch("2", route, branch); // デバッグ用
                    connectRoute(route); // 更に探索をさせる
                    // ゴール出来てないルートが返ってきた場合は
                    if ( !checkRouteConnect(route) ) {
                        //printBranch("3", route, branch); // デバッグ用
                        // 接続候補部分以降のルートをすべて削除する
                        int p = route.indexOf(b);
                        int i = 0;
                        List<Block> rms = new ArrayList<>();
                        for (Block rb : route) {
                            if (p <= i) {
                                rms.add(rb);
                            }
                            i++;
                        }
                        route.removeAll(rms);
                        //printRemove(b, rms); // デバッグ用
                    }
                    // ゴールできる場合はバックせずそのままルートを残しておく
                    //printBranch("4", route, branch); // デバッグ用
                }
            }
        }
        // ゴールできる場合はそのまま処理が終わるので、makeBlockRoute()に構成できたルートが返っていく
    }
    private boolean isStartBlock(Block block) {
        // 左端レーンかつ回路が左にある
        return circuitMap[0].contains(block) && block.getCircuit(Block.LEFT);
    }
    private boolean isEndBlock(Block block) {
        // 右端レーンかつ回路が右にある
        return circuitMap[circuitMap.length - 1].contains(block) && block.getCircuit(Block.RIGHT);
    }
    /** 生成されたルートがゴール可能かチェック **/
    private boolean checkRouteConnect(List<Block> route) {
        if( !route.isEmpty() ) {
            Block terminalBlock = route.get(route.size() - 1); // 末端のブロック
            return isEndBlock(terminalBlock); // 末端のブロックがゴールであるか
        } else {
            return false;
        }
    }

    /** 削除モーション状態における削除モーションなどの処理 **/
    private void deleting() {
        // 削除タイマーが稼働しているなら
        if( !deleteBlockTimer.isZero() ) {
            for (Block block : routeConnect) {
                // ブロックの削除モーションを稼働
                block.setDeleteMotion(deleteBlockTimer);
            }
            deleteBlockTimer.pass();
        }
        // 削除タイマーの稼働が終わったら
        else {
            deleteBlocks(routeConnect); // 右端まで届いたルートにあたるブロックを削除
            routeConnect.clear();
            rePlaceBlock(); // ブロック削除後に残ったブロックを再配置
            isDeleting = false; // 削除状態を解除
            //printDataDump(); // デバッグ用
        }
    }
    /** ルートに沿ったブロックを削除する **/
    private void deleteBlocks(List<Block> route) {
        for (List<Block> bs : circuitMap) {
            for (Block block : route) {
                bs.remove(block); // circuitMap[i]上から削除
                blocks.remove(block); // blocks上から削除
                score += block.getNum();
            }
        }
        makeCircuitTree(); // 回路ツリーの再生成
        connectCount += route.size();
    }
    /** ブロック削除後に残ったブロックを再配置 **/
    private void rePlaceBlock() {
        int posX = 0;
        for(int i = 0; i < 6; i++) {
            List<Block> bs = circuitMap[i];
            int posY = 0;
            for(Block block : bs) {
                block.setPlace(new Point(posX, DISTANCE_Y_MAX - posY));
                posY++;
            }
            posX++;
        }
    }

    /** リスタート時の初期化 **/
    private void initOnRestart() {
        blocks.clear();
        controllableBlock = null;
        nextBlock = null;
        for(List<Block> blocks : circuitMap) {
            blocks.clear();
        }
        circuitTree.clear();

        Block.resetCounter();

        isPlayingGameOverBGM = false;
        gameOverMotionTimer.reset();

        hiScore = Math.max(score, hiScore);
        score = 0;
        connectCount = 0;
    }

    // --------------------------------------------------------------- //

    // デバッグ用チート
    private void debugOnKeyPress(
            boolean isPressedD,
            boolean isPressedR,
            boolean isPressedA,
            boolean isPressedV,
            boolean isPressedM
    ) {

        // Dでコンソールにゲーム内情報を出力
        if (isPressedD) {
            printDataDump();
        }
        if (canCheat) {
            // Rでゲームの初期化
            if(isPressedR) {
                Block.resetCounter();
                bgmManager.stopSound();
                sceneTransition(Scene.GAME);
            }
            // Aでブロック全消去
            else if (isPressedA) {
                blocks.clear();
                for(List<Block> laneBlocks : circuitMap) {
                    laneBlocks.clear();
                }
                circuitTree.clear();
            }
            // Vで下層コントロ―ラの非表示・表示
            else if(isPressedV) {
                isVControllerVisible = !isVControllerVisible;
            }
            else if(isPressedM) {
                isMute = !isMute;
                bgmManager.setMute(isMute);
                seManager.setMute(isMute);
            }
        }
    }

    // コンソールにデバッグ情報を表示
    private void printDataDump() {
        MessageLogger.printMessage(this, "[DEBUG]");
        if(canCheat) {
            System.out.println("**** Variable ****");
            System.out.println("  position: [" + blockPos.x + ", " + blockPos.y + "]");
            System.out.println("  controllable Block: " + (controllableBlock != null ? controllableBlock.getNum() : -1) );
            System.out.println("  play BGM position: " + bgmManager.getPosition() );

            printCircuitMap();
            printCircuitTree();
            printConnectRoute();
        }
        System.out.println();
    }
    private void printCircuitMap() {
        System.out.println("**** Circuit Map ****");
        int posX = 0;
        for(List<Block> laneBlocks : circuitMap) {
            StringBuilder str = new StringBuilder("  [" + posX + "] ");
            for(Block b : laneBlocks) {
                str.append(b.getNum()).append(", ");
            }
            System.out.println(str);
            posX++;
        }
    }
    private void printCircuitTree() {
        System.out.println("**** Circuit Tree ****");
        Set<Block> set = circuitTree.keySet();
        for(Block b : set) {
            StringBuilder str = new StringBuilder(b.getNum() + " -> ");
            List<Block> blanch = circuitTree.get(b);
            for(Block block : blanch) {
                str.append(block.getNum()).append(", ");
            }
            System.out.println("  " + str + "(" + blanch.size() + ")");
        }
    }
    private void printConnectRoute() {
        System.out.println("**** Connect Route ****");
        List<Block> route = routeConnect;
        StringBuilder str = new StringBuilder("route: ");
        if( !route.isEmpty() ) {
            Block terminalBlock = route.get(route.size() - 1);
            for (Block block : route) {
                str.append(block.getNum());
                if(block != terminalBlock) {
                    str.append(" -> ");
                }
            }
        }
        System.out.println("  " + str);
    }
    private void printRoute(List<Block> route) {
        StringBuilder str = new StringBuilder("route(in arg): ");
        if( !route.isEmpty() ) {
            Block terminalBlock = route.get(route.size() - 1);
            for (Block block : route) {
                str.append(block.getNum());
                if(block != terminalBlock) {
                    str.append(">");
                }
            }
        }
        System.out.println("  " + str);
    }
    private void printBranch(String str, List<Block> route, List<Block> branch) {
        StringBuilder str1 = new StringBuilder();
        for (Block b : route) str1.append(b.getNum()).append(",");
        StringBuilder str2 = new StringBuilder("  <" + str + "> brc" + "[" + str1 + "] ");
        for(Block b : branch) str2.append(b.getNum()).append(",");
        System.out.println(str2);
    }

    // --------------------------------------------------------------- //

    // 描画用インスタンス
    private final GameDrawer drawer = new GameDrawer();

    private final boolean canCheat;

    // ゲーム上のオブジェクト
    private final List<Block> blocks = new ArrayList<>();
    private Block controllableBlock;
    private Block nextBlock;
    private final List<Block>[] circuitMap; // 左から[x]列目にList<Block>を格納
    private final Map<Block, List<Block>> circuitTree;
    private List<Block> routeConnect;
    private final NormalWall wallFloor, wallRoof;
    private final ElectricWall wallLeft, wallRight;
    private final VirtualController vController;

    // タイマー
    private final FrameTimer readyTimer = new FrameTimer(50, 280);
    private final FrameTimer fallTimer = new FrameTimer(50, 80);
    private final FrameTimer pauseCoolTimer = new FrameTimer(50, 90);
    private final FrameTimer deleteBlockTimer = new FrameTimer(50, 60);
    private final FrameTimer gameOverMotionTimer = new FrameTimer(50, 60);

    // ステージ定義・定数
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int DISTANCE_Y_MAX = 9;
    private static final int INITIAL_POS_X = 2;
    private static final int INITIAL_POS_Y = 0;
    private final Point nextBlockPos = new Point(680, 120);

    public enum State {
        READY,
        PLAYING,
        CLEAR,
        GAME_OVER,
        PAUSE
    }

    private final SoundEffectManager seManager;
    private final BackGroundMusicManager bgmManager;
    private final String seMoveBlock;
    private final String seDeleteBlock;
    private final String bgmGameStart;
    private final String bgmGameOver;
    private final String bgmFailed;
    private final String bgmPause;
    private final String bgm01;
    private static final int BGM01_LOOP_POSITION = 65 * 44100 + 20700;

    // 変数
    private State gameState;
    private final Point blockPos;   // 仮想位置
    private int distanceY;          // 地面（直下のブロック）までの仮想Y距離
    private int nextPatternNum;
    private boolean isReady;
    private boolean isDeleting;
    private boolean isPlayingGameOverBGM;
    private int score;
    private int hiScore;
    private int connectCount;
    private boolean isVControllerVisible;
    private boolean isMute;
    private int pauseBGMPosition;

    // キーアサイン、マウスボタンアサインの初期化
    private static final List<Integer> KEY_ASSIGN = Arrays.asList(
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_ENTER, KeyEvent.VK_P,
            KeyEvent.VK_D,
            KeyEvent.VK_R, KeyEvent.VK_A, KeyEvent.VK_V, KeyEvent.VK_M
    );
    private static final List<Integer> MOUSE_ASSIGN = List.of();
}
