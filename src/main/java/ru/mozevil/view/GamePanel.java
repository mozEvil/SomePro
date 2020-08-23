package ru.mozevil.view;

import ru.mozevil.controller.PokerController;
import ru.mozevil.model.*;
import ru.mozevil.model.factory.DF;
import ru.mozevil.model.factory.HANDS;
import ru.mozevil.model.positions.ActionNumber;
import ru.mozevil.model.positions.PositionAct_9pl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;


public class GamePanel extends JPanel {

    private Environment env;
    private final PokerController controller;
    private final Rectangle start;
    private final Rectangle stop;

    public GamePanel(PokerController controller) {
        this.controller = controller;
        start = new Rectangle(10, 10, 19, 19);
        stop = new Rectangle(40, 10, 19, 19);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (start.contains(e.getPoint())) {
                    if (controller == null) return;
                    if (!controller.isBotStarted()) controller.startBot();
                    repaint();
//                    testView();
                }
                if (stop.contains(e.getPoint())) {
                    if (controller == null) return;
                    if (controller.isBotStarted()) controller.stopBot();
                    repaint();
                }
            }
        });
    }

    private void testView() {
        LinkedList<Card> deck = new LinkedList<>(Arrays.asList(Card.values()));
        Collections.shuffle(deck);

        Environment env2 = new Environment();
        Table table = new Table();
        Seat[] seats = new Seat[9];
        for (int i = 0; i < 9; i++) {
            seats[i] = new Seat();
            seats[i].setPlayer(new Player());
            seats[i].setEmpty(false);
//            seats[i].setEmpty(Math.round(Math.random()) == 1);
//            seats[i].getPlayer().setActive(Math.round(Math.random()) == 1);
            seats[i].getPlayer().setActive(true);
            seats[i].getPlayer().setStackSize(Math.round(50 * Math.random()));
            seats[i].getPlayer().setBetSize(Math.round(10 * Math.random()));
//            seats[i].getPlayer().setHand(HANDS.GET.hand(deck.pop(), deck.pop()));
            seats[i].getPlayer().setHand(null);

        }
        seats[0].getPlayer().setHand(HANDS.GET.hand(deck.pop(), deck.pop()));
        seats[0].getPlayer().setActive(true);
        seats[0].setEmpty(false);
        // присваиваем позиции игрокам
        int index = (int) Math.round(Math.random() * 8);
        ActionNumber act = PositionAct_9pl.ACT_9;
        seats[index].getPlayer().setAct(act);
        for (int i = 0; i < 8; i++) {
            index = index + 1 == 9 ? 0 : index + 1;
            if (!seats[index].isEmpty()) {
                act = act.nextAct();
                seats[index].getPlayer().setAct(act);
            }
        }

        Card[] tableCards = new Card[5];
        tableCards[0] = deck.pop();
        tableCards[1] = deck.pop();
        tableCards[2] = deck.pop();
        tableCards[3] = deck.pop();
        tableCards[4] = deck.pop();

        table.setPotSize(Math.round(100 * Math.random()));
        table.setTableCards(tableCards);
        table.setSeats(seats);

        env2.setTable(table);
        env2.setDecision(DF.random());

        update(env2);
    }

    public void update(Environment env) {
        this.env = env;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(700, 500);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        paintTable(g2d);

        if (controller != null) paintControl(controller.isBotStarted(), g2d);

        if (env != null) {
            Seat[] seats = env.getSeats();
            if (!seats[0].isEmpty()) paintPlayer(300, 400, seats[0].getPlayer(), g2d);
            else paintPlayer(300, 400, null, g2d);
            if (!seats[1].isEmpty()) paintPlayer(160, 350, seats[1].getPlayer(), g2d);
            else paintPlayer(160, 350, null, g2d);
            if (!seats[2].isEmpty()) paintPlayer(40, 250, seats[2].getPlayer(), g2d);
            else paintPlayer(40, 250, null, g2d);
            if (!seats[3].isEmpty()) paintPlayer(110, 120, seats[3].getPlayer(), g2d);
            else paintPlayer(110, 120, null, g2d);
            if (!seats[4].isEmpty()) paintPlayer(240, 60, seats[4].getPlayer(), g2d);
            else paintPlayer(240, 60, null, g2d);
            if (!seats[5].isEmpty()) paintPlayer(360, 60, seats[5].getPlayer(), g2d);
            else paintPlayer(360, 60, null, g2d);
            if (!seats[6].isEmpty()) paintPlayer(490, 120, seats[6].getPlayer(), g2d);
            else paintPlayer(490, 120, null, g2d);
            if (!seats[7].isEmpty()) paintPlayer(560, 250, seats[7].getPlayer(), g2d);
            else paintPlayer(560, 250, null, g2d);
            if (!seats[8].isEmpty()) paintPlayer(440, 350, seats[8].getPlayer(), g2d);
            else paintPlayer(440, 350, null, g2d);

            // карты стола
            for (int i = 0; i < 5; i++) {
                Card card = env.getTableCards()[i];
                paintCard(235 + (i * 42), 180, card, g2d);
            }
            // банк
            paintText(290, 140, "Pot: " + env.getPotSize(), 100, g2d);
            // решение
            paintText(280, 280, "" + env.getDecision(), 130, g2d);
            // лучшее комбо
            paintCombo(5, 445, env.getBestCombo(), g2d);
        }

        g2d.dispose();
    }

    private void paintTable(Graphics2D g2d) {
        Ellipse2D el = new Ellipse2D.Double(48, 50, 600, 400);
        Color color = new Color(35, 104, 37);
        g2d.setColor(color);
        g2d.fill(el);
        g2d.setColor(Color.BLACK);
        g2d.draw(el);
    }

    private void paintPlayer(int x, int y, Player player, Graphics2D g2d) {
        if (player == null) {
            paintTextGray(x, y - 20, "", 82, g2d);
            paintTextGray(x, y, "", 82, g2d);
            paintTextGray(x, y + 20, "", 82, g2d);
        } else {
            paintText(x, y - 20, "Bet: " + player.getBetSize(), 82, g2d);
            paintText(x, y, "" + player.getStackSize(), 82, g2d);
            paintText(x, y + 20, "" + player.getPosition(), 82, g2d);
            if (player.isActive()) {
                paintHand(x, y - 70, player.getHand(), g2d);
            }
        }
    }

    private void paintHand(int x, int y , Hand hand, Graphics2D g2d) {
        if (hand == null) {
            paintUnknownCard(x, y, g2d);
            paintUnknownCard(x + 42, y, g2d);
        } else {
            paintCard(x, y, hand.getCard1(), g2d);
            paintCard(x + 42, y, hand.getCard2(), g2d);
        }
    }

    private void paintCard(int x, int y, Card card, Graphics2D g2d) {

        if (card == null) {
            paintUnknownCard(x, y, g2d);
            return;
        }

        Rectangle bounds = new Rectangle(x, y, 40, 50);

        int fix = 0;

        Font font = new Font("Arial", Font.BOLD, 28);

        Color clubs = new Color(63, 125, 18);
        Color diamonds = new Color(8, 93, 156);
        Color spades = new Color(40, 43, 40);
        Color hearts = new Color(161, 19, 19);

        switch (card.getSuit()){
            case CLUBS: g2d.setColor(clubs); fix = -1; break;
            case DIAMONDS: g2d.setColor(diamonds); fix = 2; break;
            case SPADES: g2d.setColor(spades); fix = 2; break;
            case HEARTS: g2d.setColor(hearts); break;
        }

        g2d.fill(bounds);
        g2d.setColor(Color.black);
        g2d.draw(bounds);

        Graphics2D copy = (Graphics2D) g2d.create();
        copy.translate(bounds.x + 5, bounds.y + 5);
        String rank = card.getRank().getName();
        String suit = card.getSuit().getIcon();

        copy.setFont(font);
        copy.setColor(Color.black);
        copy.drawString(rank, 8, 20);
        copy.drawString(suit, 8 + fix, 43);
        copy.setColor(Color.white);;
        copy.drawString(rank, 7, 19);
        copy.drawString(suit, 7 + fix, 42);

        copy.dispose();
    }

    private void paintUnknownCard(int x, int y, Graphics2D g2d) {
        Rectangle bounds = new Rectangle(x, y, 40, 50);
        Rectangle inside = new Rectangle(x+5, y+5, 30, 40);

        Color shirt = new Color(217, 77, 12);
        Color ins = new Color(208, 160, 20);
        Color romb = new Color(146, 8, 8);

        g2d.setColor(ins);
        g2d.fill(bounds);

        g2d.setColor(shirt);
        g2d.fill(inside);

        g2d.setColor(Color.black);
        g2d.draw(bounds);
        g2d.draw(inside);

        g2d.setColor(romb);
        g2d.drawLine(x + 6, y + 6, x + 34,  y + 44);
        g2d.drawLine(x + 15, y + 6, x + 34,  y + 31);
        g2d.drawLine(x + 25, y + 6, x + 34,  y + 18);
        g2d.drawLine(x + 34, y + 6, x + 6,  y + 44);
        g2d.drawLine(x + 25, y + 6, x + 6,  y + 31);
        g2d.drawLine(x + 15, y + 6, x + 6,  y + 18);
        g2d.drawLine(x + 6, y + 18, x + 25,  y + 44);
        g2d.drawLine(x + 6, y + 31, x + 15,  y + 44);
        g2d.drawLine(x + 34, y + 18, x + 15,  y + 44);
        g2d.drawLine(x + 34, y + 31, x + 25,  y + 44);
    }

    private void paintText(int x, int y, String text, int width, Graphics2D g2d) {
        Rectangle bounds = new Rectangle(x, y, width, 20);
        g2d.setColor(Color.white);
        g2d.fill(bounds);
        g2d.setColor(Color.black);
        g2d.draw(bounds);

        Graphics2D copy = (Graphics2D) g2d.create();
        copy.translate(bounds.x + (width >> 1) - text.length()*3.7, bounds.y + 15);

        Font font = new Font("Arial", Font.BOLD, 14);
        copy.setFont(font);
        copy.setColor(Color.black);
        copy.drawString(text, 0, 0);

        copy.dispose();

    }

    private void paintTextGray(int x, int y, String text, int width, Graphics2D g2d) {
        Rectangle bounds = new Rectangle(x, y, width, 20);
        g2d.setColor(Color.GRAY);
        g2d.fill(bounds);
        g2d.setColor(Color.black);
        g2d.draw(bounds);

        Graphics2D copy = (Graphics2D) g2d.create();
        copy.translate(bounds.x + (width >> 1) - text.length()*3.3, bounds.y + 15);

        Font font = new Font("Arial", Font.BOLD, 14);
        copy.setFont(font);
        copy.setColor(Color.black);
        copy.drawString(text, 0, 0);

        copy.dispose();

    }

    private void paintCombo(int x, int y, Combination combo, Graphics2D g2d) {
        if (combo == null) return;

        Graphics2D copy = (Graphics2D) g2d.create();
        copy.translate(x + 5, y + 16);

        Font font = new Font("Arial", Font.BOLD, 16);
        copy.setFont(font);
        copy.setColor(Color.black);
        copy.drawString(combo.toString(), 0, 0);

        copy.dispose();

    }

    private void paintControl(boolean isStarted, Graphics2D g2d) {
        int pad = 5;
        int w = Math.abs(stop.x - start.x) + start.width + pad;
        int h = Math.abs(stop.y - start.y) + start.height + pad;
        Rectangle block = new Rectangle(start.x - pad, start.y - pad, w + pad, h + pad);

        g2d.setColor(Color.BLACK);
        g2d.draw(block);

        if (isStarted) {
            Font fontStop = new Font("Arial", Font.BOLD, 40);
            g2d.setFont(fontStop);
            g2d.translate(stop.x - 3, stop.y + 19 );
            g2d.setColor(Color.BLACK);
            g2d.drawString("■", 1, 1);
            g2d.drawString("■", -1, -1);
            g2d.drawString("■", 1, -1);
            g2d.drawString("■", -1, 1);
            g2d.setColor(Color.RED);
            g2d.drawString("■", 0, 0);

        } else {
            Font fontStart = new Font("Arial", Font.BOLD, 28);
            g2d.setFont(fontStart);
            g2d.translate(start.x - 3, start.y + 19);
            g2d.setColor(Color.BLACK);
            g2d.drawString("►", 1, 1);
            g2d.drawString("►", -1, -1);
            g2d.drawString("►", 1, -1);
            g2d.drawString("►", -1, 1);
            g2d.setColor(Color.GREEN);
            g2d.drawString("►", 0, 0);

        }

    }

}