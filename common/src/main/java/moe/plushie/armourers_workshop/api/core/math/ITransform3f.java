package moe.plushie.armourers_workshop.api.core.math;

/**
 * <a href="https://learnopengl.com/Getting-started/Transformations">ref1</a>
 * <a href="https://stackoverflow.com/questions/58398147/what-is-the-correct-order-of-transformations-when-calculating-matrices-in-opengl">ref2</a>
 * M = T * P * R * -P * S * V
 */
public interface ITransform3f extends ITransform {

    boolean isIdentity();

    IVector3f translate();

    IVector3f rotation();

    IVector3f scale();

    IVector3f afterTranslate();

    IVector3f pivot();
}
