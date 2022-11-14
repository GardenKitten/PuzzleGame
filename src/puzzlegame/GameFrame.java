package puzzlegame;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;


public class GameFrame extends JFrame implements KeyListener, ActionListener {
    //宽高常量
    private final int width = 124, height = 124;
    //图片索引数组
    private int[][] index = new int[4][4];
    //正确的图片索引数组
    private int[][] correctIndex = new int[][]{
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}};
    //图片路径数组
    private String[] charaPath = {"Dante1", "Dante2", "Nero1", "Nero2", "Nero3", "V", "Vergil1", "Vergil2"};
    private String[] memePath = {"Meme1", "Meme2", "Meme3", "Meme4", "Meme5", "Meme6"};

    //空白图片所在坐标
    int zeroX, zeroY;

    //步数统计
    private int count = 0;

    //拼图图片路径
    private String imgPath = "img/Nero1/";

    //开场声音文件路径
    private String openingSoundPath = "sound/opening/";

    //创建三级菜单
    JMenuItem restartItem = new JMenuItem("Restart");
    JMenuItem exitItem = new JMenuItem("Exit");
    JMenuItem changeCharaItem = new JMenuItem("Characters");
    JMenuItem changeMemeItem = new JMenuItem("Meme");

    JMenuItem officialItem = new JMenuItem("Official Website");
    JMenuItem doujinItem = new JMenuItem("Unofficial Stuff");

    //角色和meme代表值
    private static final int MEME = 0;
    private static final int CHARACTER = 1;

    //键码值
    private static final int LEFTWARD = 37;
    private static final int UPWARD = 38;
    private static final int RIGHTWARD = 39;
    private static final int DOWNWARD = 40;
    private static final int SPACEBAR = 32;
    private static final int CONTROL = 17;

    public GameFrame() {
        //初始化界面
        initJFrame();

        //创建菜单
        initJMenuBar();

        //初始化图片索引
        initIndex();

        //加载图片
        initImg();

        //播放开场语音
        playOpeningSound();

        //循环播放bgm
        loopBgm();

        this.setVisible(true);

    }

    private void initIndex() {
        Random r = new Random();
        int[] tempIndex = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};
        //打乱索引
        for (int i = 0; i < tempIndex.length; i++) {
            int randomIndex = r.nextInt(tempIndex.length);
            int temp = tempIndex[i];
            tempIndex[i] = tempIndex[randomIndex];
            tempIndex[randomIndex] = temp;
        }
        //将打乱的数字放入二维数组
        for (int i = 0; i < tempIndex.length; i++) {
            index[i / 4][i % 4] = tempIndex[i];
        }
    }

    private void initImg() {
        //清空页面
        this.getContentPane().removeAll();

        //判断游戏是否结束,如结束显示胜利图标
        if (ifWin()) {
            //显示胜利图标
            ImageIcon temp = new ImageIcon("img/sss.png");
            Image img = temp.getImage();
            img = img.getScaledInstance(350, 350, Image.SCALE_AREA_AVERAGING);
            temp.setImage(img);

            JLabel winImg = new JLabel(temp);
            winImg.setBounds(135, 230, 350, 350);
            this.getContentPane().add(winImg);

            playWinSound();
        }

        //显示步数
        JLabel step = new JLabel("Steps:  " + count);
        step.setBounds(470, 100, 110, 40);
        //设置字体和颜色
        Font font = new Font(Font.SERIF, Font.PLAIN, 22);
        step.setFont(font);
        this.getContentPane().add(step);

        //添加边框
        JLabel border = new JLabel(new ImageIcon("img/border.png"));
        border.setBounds(0, 0, 603, 680);
        this.getContentPane().add(border);

        //打印图片
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int number = index[i][j];

                //记录空白图片坐标
                if (number == 0) {
                    zeroX = i;
                    zeroY = j;
                    continue;
                }

                ImageIcon imageIcon = new ImageIcon(imgPath + number + ".jpg");
                Image img = imageIcon.getImage();
                //对图像进行缩放
                img = img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
                imageIcon.setImage(img);

                JLabel jLabel = new JLabel(imageIcon);
                jLabel.setBounds(j * width + 54, i * height + 153, width, height);

                //给小图片添加边框
                //Color red = new Color(255, 13, 41);
                //Color blue = new Color(18, 243, 246);
                //jLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, blue.darker(), red.darker()));

                //将图片添加到大容器中
                this.getContentPane().add(jLabel);
            }
        }

        //添加背景
        JLabel background = new JLabel(new ImageIcon("img/bg.png"));
        background.setBounds(0, 0, 603, 680);
        this.getContentPane().add(background);

        //刷新页面
        this.getContentPane().repaint();
    }

    private void initJMenuBar() {
        //创建总菜单栏
        JMenuBar jMenuBar = new JMenuBar();

        //创建一级菜单
        JMenu functionJMenu = new JMenu("Function");
        JMenu aboutJMenu = new JMenu("About");
        JMenu changeItem = new JMenu("Change the Picture");

        //菜单绑定事件
        restartItem.addActionListener(this);
        exitItem.addActionListener(this);
        officialItem.addActionListener(this);
        doujinItem.addActionListener(this);

        changeCharaItem.addActionListener(this);
        changeMemeItem.addActionListener(this);

        //将子菜单添加到父级中
        changeItem.add(changeCharaItem);
        changeItem.add(changeMemeItem);

        jMenuBar.add(functionJMenu);
        jMenuBar.add(aboutJMenu);

        functionJMenu.add(changeItem);
        functionJMenu.add(restartItem);
        functionJMenu.add(exitItem);

        aboutJMenu.add(officialItem);
        aboutJMenu.add(doujinItem);

        //给整个页面添加菜单
        this.setJMenuBar(jMenuBar);
    }

    private void initJFrame() {
        this.setSize(615, 735);
        this.setTitle("Puzzle May Cry");
        this.setAlwaysOnTop(true);
        //设置居中
        this.setLocationRelativeTo(null);
        //设置关闭模式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //取消默认的居中放置
        this.setLayout(null);
        this.getContentPane().setLayout(null);
        //禁用最大化和拉伸
        this.setResizable(false);
        //给界面添加键盘监听
        this.addKeyListener(this);
    }

    private void initPath(int num) {
        Random r = new Random();
        int length;
        if (num == CHARACTER) {
            length = charaPath.length;
            imgPath = "img/" + charaPath[r.nextInt(length)] + "/";
        } else if (num == MEME) {
            length = memePath.length;
            imgPath = "img/" + memePath[r.nextInt(length)] + "/";
        }
    }

    public boolean ifWin() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (index[i][j] != correctIndex[i][j])
                    return false;
            }
        }
        return true;
    }

    public void loopBgm() {
        try {
            File bgmFile = new File("sound/bgm.wav");
            AudioInputStream auInput = AudioSystem.getAudioInputStream(bgmFile);
            Clip bgm = AudioSystem.getClip();
            bgm.open(auInput);
            bgm.start();
            bgm.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void playOpeningSound() {
        try {
            Random r = new Random();
            int num = r.nextInt(13) + 1;
            File openingSoundFile = new File(openingSoundPath + num + ".wav");
            AudioInputStream auInput = AudioSystem.getAudioInputStream(openingSoundFile);
            Clip opening = AudioSystem.getClip();
            opening.open(auInput);
            opening.start();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void playSoundEffect() {
        try {
            File soundEffectFile = new File("sound/soundEffect.wav");
            AudioInputStream auInput = AudioSystem.getAudioInputStream(soundEffectFile);
            Clip sound = AudioSystem.getClip();
            sound.open(auInput);
            sound.start();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void playWinSound() {
        try {
            File winSoundFile = new File("sound/winSound.wav");
            AudioInputStream auInput = AudioSystem.getAudioInputStream(winSoundFile);
            Clip winSound = AudioSystem.getClip();
            winSound.open(auInput);
            winSound.start();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (ifWin())
            return;

        int code = e.getKeyCode();

        //显示全图
        if (code == SPACEBAR) {
            this.getContentPane().removeAll();

            //对图像进行缩放
            ImageIcon temp = new ImageIcon(imgPath + "all.jpg");
            Image img = temp.getImage();
            img = img.getScaledInstance(width * 4, height * 4, Image.SCALE_AREA_AVERAGING);
            temp.setImage(img);

            JLabel fullImg = new JLabel(temp);
            fullImg.setBounds(54, 153, width * 4, height * 4);
            this.getContentPane().add(fullImg);

            //添加背景
            JLabel background = new JLabel(new ImageIcon("img/bg.png"));
            background.setBounds(0, 0, 603, 680);
            this.getContentPane().add(background);

            this.getContentPane().repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (ifWin())
            return;

        int code = e.getKeyCode();
        switch (code) {
            case LEFTWARD:
                if (zeroY == 3)
                    return;

                index[zeroX][zeroY] = index[zeroX][zeroY + 1];
                index[zeroX][zeroY + 1] = 0;
                zeroY++;
                count++;

                playSoundEffect();
                initImg();
                break;

            case UPWARD:
                if (zeroX == 3)
                    return;

                index[zeroX][zeroY] = index[zeroX + 1][zeroY];
                index[zeroX + 1][zeroY] = 0;
                zeroX++;
                count++;

                playSoundEffect();
                initImg();
                break;

            case RIGHTWARD:
                if (zeroY == 0)
                    return;

                index[zeroX][zeroY] = index[zeroX][zeroY - 1];
                index[zeroX][zeroY - 1] = 0;
                zeroY--;
                count++;

                playSoundEffect();
                initImg();
                break;

            case DOWNWARD:
                if (zeroX == 0)
                    return;

                index[zeroX][zeroY] = index[zeroX - 1][zeroY];
                index[zeroX - 1][zeroY] = 0;
                zeroX--;
                count++;

                playSoundEffect();
                initImg();
                break;

            //重新显示小图片
            case SPACEBAR:
                initImg();
                break;

            //直接完成游戏
            case CONTROL:
                index = new int[][]{
                        {1, 2, 3, 4},
                        {5, 6, 7, 8},
                        {9, 10, 11, 12},
                        {13, 14, 15, 0}};
                initImg();
                break;

            default:
                break;

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (restartItem == obj) {
            count = 0;
            initIndex();
            initImg();
        } else if (changeCharaItem == obj) {
            initPath(CHARACTER);
            count = 0;
            initIndex();
            initImg();
        } else if (changeMemeItem == obj) {
            initPath(MEME);
            count = 0;
            initIndex();
            initImg();
        } else if (exitItem == obj) {
            System.exit(0);
        } else if (officialItem == obj) {
            String website = "cmd /c start https://www.devilmaycry5.com/us/";
            try {
                Runtime.getRuntime().exec(website);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if (doujinItem == obj) {
            String website = "cmd /c start http://htmlpreview.github.io/?https://github.com/GardenKitten/devil-may-cry/blob/main/dmc/html/title.html";
            try {
                Runtime.getRuntime().exec(website);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
