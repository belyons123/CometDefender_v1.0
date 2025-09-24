import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;

public class Game extends JPanel implements ActionListener, KeyListener, MouseListener {
    private Timer timer;
    private java.util.List<Circle> circles = new ArrayList<>();
    private java.util.List<Square> squares = new ArrayList<>();
    private java.util.List<Projectile> projectiles = new ArrayList<>();
    Random random = new Random();
    private static int playerHP = 1;
    private static JLabel pHP = new JLabel("Player HP: "+ playerHP);
    
    private boolean ShieldLeft = false;
    private boolean ShieldRight = false;
    
    private static boolean poweredUp = false;
    private static boolean gameOver = false;
    
    private static Boss b = new Boss(330);
    private static JLabel bHP = new JLabel ("Boss HP: " + b.bhp);
    
    private static String winlose = "";
    private static JLabel gameOverScrn = new JLabel("Game Over: " + winlose);
    
    public Game() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);
        timer = new Timer(50, this);
        timer.start();
    }
    
    //key events to move shield
    @Override
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        if( key == KeyEvent.VK_A)
        {
            ShieldLeft=true; 
        }
        if( key == KeyEvent.VK_D)
        {
            ShieldRight=true; 
        }
        repaint();
    }
    @Override 
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if( key == KeyEvent.VK_A)
        {
            ShieldLeft=false; 
        }
        if( key == KeyEvent.VK_D)
        {
            ShieldRight=false; 
        }
        repaint();
    }
    @Override public void keyTyped(KeyEvent e){}
    
    //mouse events for projectile firing
    @Override public void mouseClicked(MouseEvent e){}
    @Override 
    public void mousePressed(MouseEvent e)
    {
        int mX = e.getX();
        int mY = e.getY();
        int startX = 350;
        int startY = 620;
        projectiles.add(new Projectile(startX, startY, mX, mY));
        repaint();
    }
    @Override public void mouseEntered(MouseEvent e){}
    @Override public void mouseExited(MouseEvent e){}
    @Override public void mouseReleased(MouseEvent e){}
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        update();
        repaint();
    }

    //changes made at end of each refresh
    private void update() 
    {
        if(gameOver == false){
        //random circle generation
        if(random.nextInt(22) < 1)
        {
            circles.add(new Circle(random.nextInt(getWidth()), 0));
        }
        if(random.nextInt(320)<1)
        {
            squares.add(new Square(random.nextInt(getWidth()), 0));
        }
        
        //moving circles down
        Iterator<Circle> iterator = circles.iterator();
        while (iterator.hasNext())
        {
            Circle c = iterator.next();
            c.y += c.speed;
            
            if(c.y > 500 && c.x >= rectX && c.x <= rectX+150)
            {
                iterator.remove();
            }
            else if(c.y > getHeight())
            {
                iterator.remove();
                playerHP--;
            }
        }
        
        //moving power squares down
        Iterator<Square> iteratorS = squares.iterator();
        while (iteratorS.hasNext())
        {
            Square s = iteratorS.next();
            s.y+=s.speed;
            if(s.y >500 && s.x >= rectX && s.x<=rectX+150)
            {
                iteratorS.remove();
            }
            else if(s.y>getHeight())
            {
                activatePowerup();
                iteratorS.remove();
            }
        }
        
        //moving the shield
        if(ShieldLeft)
        {
            rectX-=25;
        }
        if(ShieldRight)
        {
            rectX+=25;
        }
        rectX = Math.max(0, Math.min(rectX, getWidth() - 150));
        
        //Moving the projectile
        Iterator<Projectile>  iteratorP = projectiles.iterator();
        while(iteratorP.hasNext())
        {
            Projectile p = iteratorP.next();            
            p.x+=p.dX;
            p.y+=p.dY;
            
            if(poweredUp)
            {
                Iterator<Circle> iteratorC = circles.iterator();
                while(iteratorC.hasNext())
                {
                    Circle c = iteratorC.next();
                    double dx = p.x-c.x;
                    double dy = p.y-c.y;
                    if(Math.sqrt(dx*dx + dy*dy) < 27)
                    {
                        iteratorC.remove();
                        break;
                    }
                }
            }
            if(p.x>b.x && p.x<b.x+50 && p.y<50)
            {
                if(poweredUp)
                {
                    b.bhp-=3;
                }
                else
                {
                    b.bhp-=1;
                }
                iteratorP.remove(); 
                break;
            }
            if(p.x>getWidth() || p.x<0 || p.y>getHeight() || p.y<0)
            {
                iteratorP.remove();
            }
            
        }
        
        //Code for boss' movement
        if(b.bhp > 0)
        {
            int rand = (int)(Math.random()*10);
            if(rand%2==0 && b.x<= 650)
            {
                b.x+=b.speed;
            }
            else if(b.x<=0)
            {
                b.x+=b.speed;
            }
            else
            {
                b.x-=b.speed;
            }
        }
        
        //print * update player hp
        pHP.setText("Player HP: "+playerHP);
        
        //print & update boss hp, bHP
        bHP.setText("Boss HP: "+b.bhp);
        
        if(playerHP <= 0 || b.bhp <=0)
            {
                gameOver = true;
            }
        }
        else
        {
            if(playerHP <= 0)
            {
                winlose = "You Win!";
            }
            else
            {
                winlose = "You Lose!";
            }
        }
    }
    
    //Defining activate powerup for the power squares
    public void activatePowerup()
    {
        if(!poweredUp)
        {
            poweredUp=true;
            Timer powerupTimer = new Timer(6000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                poweredUp = false;
                }
            });
            powerupTimer.setRepeats(false);// Only run once
            powerupTimer.start();
        }
    }

    //this is the shields initial x value and below is where everything is painted
    private int rectX = 275;
    @Override //sprites and background here
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        //painting background gradient
        
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), Color.DARK_GRAY);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        //painting buildings in the background
        g2.setColor(Color.GRAY);
        g2.fillRect(40, 550, 30, 150);
        g2.fillRect(90, 580, 24, 120);
        g2.fillRect(140, 510, 28, 190);
        g2.fillRect(160, 600, 27, 100);
        g2.fillRect(180, 530, 25, 170);
        g2.fillRect(210, 590, 20, 110);
        g2.fillRect(230, 500, 26, 200);
        g2.fillRect(290, 580, 30, 120);
        g2.fillRect(400, 550, 25, 150);
        g2.fillRect(440, 510, 28, 190);
        g2.fillRect(480, 590, 40, 110);
        g2.fillRect(520, 530, 27, 170);
        g2.fillRect(560, 540, 25, 160);
        g2.fillRect(600, 550, 25, 150);
        g2.fillRect(640, 520, 26, 180);
        
        //this triangle represents the player
        int[] xTri = {350, 400, 300};
        int[] yTri = {600, 670, 670};
        g2.setColor(Color.BLUE);
        g2.fillPolygon(xTri, yTri, 3);
        
        //this is the shield
        g2.fillRect(rectX, 550, 150, 20);
        
        //Painting the comets falling down
        for (Circle c : circles) {
            g.setColor(c.color);
            g.fillOval(c.x, c.y, c.size, c.size);
        }
        
        //painting power squares falling
        for(Square s: squares){
            g.setColor(s.color);
            g.fillRect(s.x, s.y, s.size, s.size);
        }
        
        //Painting the player's projectiles
        for (Projectile p : projectiles) {
            p.draw((Graphics2D) g);
            g.setColor(p.color);
            if(poweredUp)
            {
                g.setColor(Color.YELLOW);
            }
            g.fillOval((int)p.x, (int)p.y, p.size, p.size);
        }
        
        //painting the boss flying overhead
        g.setColor(b.color);
        g.fillRect(b.x, 50, b.width, b.height);
        
    }

    static class Circle {
        int x, y, size, speed;
        Color color;

        public Circle(int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.size = 50; //+ (int)(Math.random() * 30); // random size
            this.speed = 2 + (int)(Math.random() * 5);  // random speed
            this.color = Color.RED; //new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        }
    }
    
    static class Square
    {
        int x, y, size, speed;
        Color color;
        
        public Square(int startX, int startY)
        {
            this.x = startX;
            this.y = startY;
            this.size = 40;
            this.speed = 4 + (int)(Math.random() * 5); 
            this.color = Color.YELLOW; 
        }
    }
    
    static class Projectile
    {
        double x, y, dX, dY;
        Color color;
        int size;
        
        public Projectile(int startX, int startY, int endX, int endY)
        {
            this.x = startX;
            this.y = startY;
            this.size = 25;
            this.color = Color.BLUE;
            int ySlope = endY-startY;
            int xSlope = endX-startX;
            double distance = Math.sqrt(xSlope * xSlope + ySlope * ySlope);
            double speed = 40.0;
            this.dX = (xSlope / distance) * speed;
            this.dY = (ySlope / distance) * speed;
        }
        
        public void draw(Graphics2D g2) 
        {
            g2.setColor(color);
            if(poweredUp)
            {
                g2.setColor(Color.YELLOW);
            }
            g2.fillOval((int)x, (int)y, size, size);
        }

    }
    
    static class Boss
    {
        int x, width, height, speed, bhp;
        Color color;
        public Boss(int startX)
        {
            this.x = startX;
            this.width = 50;
            this.height = 20;
            this.speed = 25;
            this.bhp = 100;
            this.color = Color.ORANGE;
        }
    }

    
    public static void main(String[] args) 
    {
        JFrame window = new JFrame("CometDefender");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(700, 700);
        window.getContentPane().setBackground(Color.BLACK);

        
        Game game = new Game();
        window.add(game);

        window.setVisible(true);
        
        
        pHP.setForeground(Color.GREEN);
        window.add(pHP,BorderLayout.SOUTH);
        
        bHP.setForeground(Color.ORANGE);
        window.add(bHP, BorderLayout.NORTH);
        
        if(gameOver == true)
        {
            gameOverScrn.setForeground(Color.YELLOW);
            window.add(gameOverScrn, BorderLayout.CENTER);
            gameOverScrn.setVisible(true);
            window.revalidate();
            window.repaint();
        }
    }
}