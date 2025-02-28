package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.ClipContextImpl;
import com.apple.library.impl.GraphicsContextImpl;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class CGGraphicsContext implements GraphicsContextImpl {

    private final CGGraphicsState state;
    private final CGGraphicsRenderer renderer;

    private final ClipContextImpl clipContext;

    public CGGraphicsContext(CGGraphicsState state, CGGraphicsRenderer renderer) {
        this.state = state;
        this.renderer = renderer;
        this.clipContext = ClipContextImpl.getInstance();
    }

    public void drawImage(UIImage image, CGRect rect) {
        if (image == null) {
            return;
        }
        float u = 0, v = 0, w = rect.width(), h = rect.height(), mw = 256, mh = 256;
        var texturePos = image.uv();
        if (texturePos != null) {
            u = texturePos.x();
            v = texturePos.y();
        }
        var size = image.size();
        if (size != null) {
            w = size.width();
            h = size.height();
        }
        var limitSize = image.limit();
        if (limitSize != null) {
            mw = limitSize.width();
            mh = limitSize.height();
        }
        var animation = image.animationData();
        if (animation != null && animation.frames != 0) {
            int frame = (int) ((System.currentTimeMillis() / animation.speed) % animation.frames);
            v += h * frame;
        }
        var clipData = image.clipData();
        if (clipData != null) {
            float t = clipData.contentInsets.top;
            float b = clipData.contentInsets.bottom;
            float l = clipData.contentInsets.left;
            float r = clipData.contentInsets.right;
            drawTilableImage(image.rl(), rect.x, rect.y, rect.width, rect.height, u, v, w, h, mw, mh, t, b, l, r, 0);
            return;
        }
        var sourceSize = image.source();
        if (sourceSize != null) {
            float sw = sourceSize.width;
            float sh = sourceSize.height;
            drawResizableImage(image.rl(), rect.x, rect.y, w, h, u, v, sw, sh, mw, mh);
            return;
        }
        drawResizableImage(image.rl(), rect.x, rect.y, w, h, u, v, w, h, mw, mh);
    }

    public void drawText(NSString text, float x, float y, UIFont font, @Nullable UIColor color, @Nullable UIColor shadowColor) {
        if (text == null) {
            return;
        }
        if (color == null) {
            color = AppearanceImpl.DEFAULT_TEXT_COLOR;
        }
        drawText(text, x, y, color.getRGB(), shadowColor != null, font, 0);
    }

    public void drawTooltip(Object tooltip, CGRect rect) {
        if (tooltip == null) {
            return;
        }
        if (tooltip instanceof NSString text) {
            renderer.renderTooltip(text, rect, UIFont.systemFont(), this);
            return;
        }
        if (tooltip instanceof ItemStack itemStack) {
            renderer.renderTooltip(itemStack, rect, UIFont.systemFont(), this);
            return;
        }
        if (tooltip instanceof TooltipRenderer view) {
            view.render(rect, this);
            return;
        }
        // not supported tooltip content.
    }

    public void drawContents(Object contents, CGRect rect, UIView view) {
        if (contents == null) {
            return;
        }
        if (contents instanceof UIImage image) {
            drawImage(image, rect);
            return;
        }
        if (contents instanceof UIColor color) {
            fillRect(rect, color);
            return;
        }
        if (contents instanceof CGGradient gradient) {
            fillRect(gradient, rect);
            return;
        }
        // not supported contents.
    }

    public void drawEntity(Entity entity, CGPoint origin, int scale, CGPoint focus) {
        renderer.renderEntity(entity, origin, scale, focus, this);
    }

    public void drawItem(ItemStack itemStack, int x, int y) {
        renderer.renderItem(itemStack, x, y, this);
    }

    public void fillRect(CGRect rect, UIColor color) {
        if (color != null && color != UIColor.CLEAR) {
            fillRect(rect, color.getRGB());
        }
    }

    public void fillRect(CGRect rect, int color) {
        drawColor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 0, color, color);
    }

    public void fillRect(float x1, float y1, float x2, float y2, int color) {
        drawColor(x1, y1, x2, y2, 0, color, color);
    }

    public void fillRect(CGGradient gradient, CGRect rect) {
        int color1 = gradient.startColor.getRGB();
        int color2 = gradient.endColor.getRGB();
        drawColor(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), 0, color1, color2);
    }

    public void strokeRect(CGRect rect, UIColor color) {
        strokeRect(rect, 1, color.getRGB());
    }

    public void strokeRect(CGRect rect, float lineHeight, UIColor color) {
        strokeRect(rect, lineHeight, color.getRGB());
    }

    public void strokeRect(CGRect rect, float lineHeight, int rgb) {
        drawBorder(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), 0, lineHeight, rgb);
    }

    public void addClip(CGRect rect) {
        state.flush();
        clipContext.addClip(new ClipContextImpl.Rectangle(rect));
    }

    public void addClip(CGRect rect, float cornerRadius) {
        state.flush();
        clipContext.addClip(new ClipContextImpl.RoundRectangle(rect, cornerRadius));
    }

    public void removeClip() {
        state.flush();
        clipContext.removeClip();
    }

    public CGRect boundingBoxOfClipPath() {
        return clipContext.boundingBoxOfClipPath();
    }

    public void saveGraphicsState() {
        state.save();
    }

    public void translateCTM(float x, float y, float z) {
        state.translate(x, y, z);
    }

    public void scaleCTM(float x, float y, float z) {
        state.scale(x, y, z);
    }

    public void rotateCTM(float x, float y, float z) {
        state.rotate(x, y, z);
    }

    public void concatenateCTM(CGAffineTransform transform) {
        state.concatenate(transform);
    }

    public void restoreGraphicsState() {
        state.restore();
    }

    public void setBlendMode(CGBlendMode mode) {
        // TODO: impl with GL30.glBlendFuncSeparate
    }

    public void enableBlend() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void disableBlend() {
        // ..
    }

    public void setBlendColor(UIColor color) {
        RenderSystem.setShaderColor(color);
    }

    public void strokeDebugRect(int tag, CGRect rect) {
        if (ModDebugger.viewHierarchy) {
            var color = ColorUtils.getPaletteColor(tag);
            drawBorder(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), 0, color);
        }
    }

    public CGGraphicsState state() {
        return state;
    }
}
