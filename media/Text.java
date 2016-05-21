package saga.progetto.tesi.media;

import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.Font.Style;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import pythagoras.f.IPoint;
import tripleplay.util.TextConfig;

public class Text
{
	private static final String DEFAULT_FONT = "Arial";
	private ImageLayer textLayer;
	private String text;
	private Font textFont;
	private TextConfig config;
	private int color;

	public Text(String text, Style style, float size, int color, int stroke)
	{
		this.text = text;
		this.color = color;
		textFont = graphics().createFont(DEFAULT_FONT, style, size);
		config = new TextConfig(new TextFormat().withFont(textFont), color).withOutline(stroke);
		textLayer = config.toLayer(text);
		textLayer.setVisible(false);
	}
	
	public Text(String text, Style style, float size, int color)
	{
		this.text = text;
		this.color = color;
		textFont = graphics().createFont(DEFAULT_FONT, style, size);
		config = new TextConfig(new TextFormat().withFont(textFont), color);
		textLayer = config.toLayer(text);
		textLayer.setVisible(false);
	}

	public void updateText(String text)
	{
		this.text = text;
		textLayer = config.toLayer(text);
	} 
	
	public int getColor()
	{
		return color;
	}
	
	public void setVisible(boolean visible)
	{
		textLayer.setVisible(visible);
	}
	
	public boolean visible()
	{
		return textLayer.visible();
	}
	
	public void setDepth(float depth)
	{
		textLayer.setDepth(depth);
	}
	
	public float depth()
	{
		return textLayer.depth();
	}
	
	public float alpha()
	{
		return textLayer.alpha();
	}
	
	public void setAlpha(float alpha)
	{
		textLayer.setAlpha(alpha);
	}
	
	public void setTranslation(IPoint point)
	{
		textLayer.setTranslation(point.x(), point.y());
	}
	
	public void init()
	{
		graphics().rootLayer().add(textLayer);
	}
	
	public void setTranslation(float x, float y)
	{
		textLayer.setTranslation(x, y);
	}
	
	public void destroy()
	{
		textLayer.destroy();
	}
	
	public float width()
	{
		return textLayer.width();
	}
	
	public float height()
	{
		return textLayer.height();
	}
	
	public float tx()
	{
		return textLayer.tx();
	}
	
	public float ty()
	{
		return textLayer.ty();
	}
	
	public ImageLayer getLayer()
	{
		return textLayer;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
}
