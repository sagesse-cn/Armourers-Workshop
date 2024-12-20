package moe.plushie.armourers_workshop.api.core.math;

public interface IRectangle3i {

    int x();

    int y();

    int z();

    int width();

    int height();

    int depth();

    default int minX() {
        return x();
    }

    default int minY() {
        return y();
    }

    default int minZ() {
        return z();
    }

    default int midX() {
        return x() + width() / 2;
    }

    default int midY() {
        return y() + height() / 2;
    }

    default int midZ() {
        return z() + depth() / 2;
    }

    default int maxX() {
        return x() + width();
    }

    default int maxY() {
        return y() + height();
    }

    default int maxZ() {
        return z() + depth();
    }
}
