package de.syscy.bguilib.creator.guidata;

import org.w3c.dom.Element;

import de.syscy.bguilib.components.BGComponent;
import lombok.Getter;
import lombok.Setter;

public abstract class BGComponentData {
	protected @Getter @Setter int x = 0;
	protected @Getter @Setter int y = 0;
	protected @Getter @Setter int width = 1;
	protected @Getter @Setter int height = 1;

	public abstract BGComponent toBGComponent();

	public abstract BGComponentData parseXML(Element var1);

	protected void parseComponentValuesXML(Element element) {
		this.setX(Integer.parseInt(element.getAttribute("x")));
		this.setY(Integer.parseInt(element.getAttribute("y")));
		this.setWidth(Integer.parseInt(element.getAttribute("width")));
		this.setHeight(Integer.parseInt(element.getAttribute("height")));
	}

	public abstract void saveToXML(Element var1);

	protected void saveComponentValuesToXML(Element element) {
		element.setAttribute("x", String.valueOf(this.getX()));
		element.setAttribute("y", String.valueOf(this.getY()));
		element.setAttribute("width", String.valueOf(this.getWidth()));
		element.setAttribute("height", String.valueOf(this.getHeight()));
	}

	public abstract String getComponentName();
}
