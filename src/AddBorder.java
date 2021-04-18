public class AddBorder implements PixelFilter {
    private int sideSize, topBottomSize;
    private int color;

    public AddBorder() {
        sideSize = 10;
        topBottomSize = 10;
        color = 0xffffff;
    }

    public void setSideSize(int sideSize) {
        this.sideSize = sideSize;
    }

    public void setTopBottomSize(int topBottomSize) {
        this.topBottomSize = topBottomSize;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public DImage processImage(DImage img) {
        int[][] pixels = img.getColorPixelGrid();
        int[][] output = new int[pixels.length+2*topBottomSize][pixels[0].length+2* sideSize];

        fillWithColor(output, color);

        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[r].length; c++) {
                output[topBottomSize+r][sideSize+c] = pixels[r][c];
            }
        }

        img.setPixels(output);

        return img;
    }

    private void fillWithColor(int[][] output, int color) {
        for (int r = 0; r < output.length; r++) {
            for (int c = 0; c < output[r].length; c++) {
                output[r][c] = color;
            }
        }
    }
}
