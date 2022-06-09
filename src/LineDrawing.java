import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class LineDrawing implements PixelFilter, Drawable, Interactive {
    private static final int NUM_PEGS = 400;
    private static final int TOTAL_LINES = 2000;
    private static final short WHITE = 255;

    private ArrayList<Point> pegs;
    private ArrayList<Point> additionalPoints;
    private ArrayList<Line> lines;

    @Override
    public DImage processImage(DImage img) {
        short[][] bwpixels = img.getBWPixelGrid();

        short[][] bluredPixels = new short[1][1];
        if (pegs.size() < NUM_PEGS) {
            initPegs(pegs, bwpixels, NUM_PEGS);
        }

        lines = generateLines(pegs, bwpixels, bluredPixels);
        System.out.println("Lines generated!");

        fill(bwpixels, (short) 255);
        img.setPixels(bwpixels);
        return img;
    }

    private void fill(short[][] bwpixels, short color) {
        for (int r = 0; r < bwpixels.length; r++) {
            for (int c = 0; c < bwpixels[r].length; c++) {
                bwpixels[r][c] = color;
            }
        }
    }

    private ArrayList<Line> generateLines(ArrayList<Point> pegs, short[][] pixels, short[][] target) {
        ArrayList<Line> out = new ArrayList<>();
        if (pegs == null || pegs.size() == 0) return out;

        Point currentPeg = pegs.get(0);
        for (int lineNum = 0; lineNum < TOTAL_LINES; lineNum++) {
            Line next = findNextLine(currentPeg, pegs, pixels);
            subtractLine(next, pixels);
            out.add(next);
            currentPeg = next.p2;
        }

        return out;
    }

    private void subtractLine(Line line, short[][] pixels) {
        Point currentPeg = line.p1;
        Point nextPeg = line.p2;
        double totalDx = nextPeg.getCol() - currentPeg.getCol();
        double totalDy = nextPeg.getRow() - currentPeg.getRow();
        double slope = totalDy / totalDx;

        int n = 0;
        double dx = 0, dy = 0;
        if (Math.abs(slope) < 1) {
            n = (int) Math.abs(totalDx);
            dx = (totalDx > 0) ? 1 : -1;
            dy = slope * dx;
        } else {
            n = (int) Math.abs(totalDy);
            dy = (totalDy > 0) ? 1 : -1;
            dx = 1.0 / slope * dy;
        }

        double x = currentPeg.getCol();
        double y = currentPeg.getRow();
        for (int i = 0; i < n; i++) {
            pixels[(int) y][(int) x] = WHITE;

            y += dy;
            x += dx;
        }
    }

    private Line findNextLine(Point currentPeg, ArrayList<Point> pegs, short[][] pixels) {

        double darkestSoFar = 256;
        Point bestNext = null;

        for (Point nextPeg : pegs) {
            if (currentPeg.equals(nextPeg)) continue;

            double averageBrightness = brightnessOfPath(currentPeg, nextPeg, pixels);
            // System.out.println("Average brightness is: " + averageBrightness);
            if (averageBrightness < darkestSoFar) {
                darkestSoFar = averageBrightness;
                bestNext = nextPeg;
            }
        }

        return new Line(currentPeg, bestNext);
    }

    private double brightnessOfPath(Point currentPeg, Point nextPeg, short[][] pixels) {
        //       System.out.println("Brigthness from: " + currentPeg + " to " + nextPeg);
        double totalDx = nextPeg.getCol() - currentPeg.getCol();
        double totalDy = nextPeg.getRow() - currentPeg.getRow();
        double slope = totalDy / totalDx;

        int n = 0;
        double dx = 0, dy = 0;
        if (Math.abs(slope) < 1) {
            n = (int) Math.abs(totalDx);
            dx = (totalDx > 0) ? 1 : -1;
            dy = slope * dx;
        } else {
            n = (int) Math.abs(totalDy);
            dy = (totalDy > 0) ? 1 : -1;
            dx = 1.0 / slope * dy;
        }

        double totalBrightness = 0;
        int count = 0;
        double x = currentPeg.getCol();
        double y = currentPeg.getRow();
        for (int i = 0; i < n; i++) {
            count++;
            totalBrightness += pixels[(int) y][(int) x];

            y += dy;
            x += dx;
        }

        return totalBrightness / count;
    }

    private void initPegs(ArrayList<Point> pegs, short[][] bwpixels, int numPegs) {
        float midCol = (bwpixels[0].length - 1) / 2.0f;
        float midRow = (bwpixels.length - 1) / 2.0f;

        float dt = 1.0f / numPegs;
        float t = 0;
        for (int peg = 0; peg < numPegs; peg++) {
            int row = (int) (midRow + midRow * Math.sin(2 * Math.PI * t));
            int col = (int) (midCol + midCol * Math.cos(2 * Math.PI * t));
            pegs.add(new Point(row, col));
            t += dt;
        }
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        if (pegs == null) return;
        if (lines == null) return;

        window.fill(window.color(255, 0, 0));
        for (Point peg : pegs) {
            window.ellipse(peg.getCol(), peg.getRow(), 3, 3);
        }

        window.stroke(window.color(0));
        int linesToDraw = (int) (lines.size() * (window.mouseX / (double) window.width));
        window.fill(0);
        window.text("" + linesToDraw, 10, window.height - 50);
        for (int i = 0; i < linesToDraw; i++) {
            Line line = lines.get(i);
            window.line(line.p1.getCol(), line.p1.getRow(), line.p2.getCol(), line.p2.getRow());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        Point p = new Point(mouseY, mouseX);
        pegs.add(p);
    }

    @Override
    public void keyPressed(char key) {

    }
}
