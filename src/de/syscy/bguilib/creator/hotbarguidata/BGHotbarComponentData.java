package de.syscy.bguilib.creator.hotbarguidata;

import de.syscy.bguilib.components.BGHotbarComponent;
import org.w3c.dom.Element;

public abstract class BGHotbarComponentData {

   protected int x = 0;


   public abstract BGHotbarComponent toBGHotbarComponent();

   public abstract BGHotbarComponentData parseXML(Element var1);

   protected void parseComponentValuesXML(Element element) {
      this.setX(Integer.parseInt(element.getAttribute("x")));
   }

   public abstract void saveToXML(Element var1);

   protected void saveComponentValuesToXML(Element element) {
      element.setAttribute("x", String.valueOf(this.getX()));
   }

   public abstract String getComponentName();

   public void setX(int x) {
      this.x = x;
   }

   public int getX() {
      return this.x;
   }
}
