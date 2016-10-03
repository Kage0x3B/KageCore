package de.syscy.bguilib.creator.guidata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.syscy.bguilib.BGGUI;
import de.syscy.kagecore.KageCore;
import lombok.Getter;
import lombok.Setter;

public class GUIData {
	private static HashMap<String, BGComponentData> allComponents = new HashMap<>();
	private @Getter File guiFile;
	private @Getter ArrayList<BGComponentData> components = new ArrayList<>();
	private @Getter @Setter String title = "GUI";
	private @Getter @Setter int height = 6;
	private @Getter @Setter boolean deleted = false;

	static {
		allComponents.put(BGButtonData.componentName, new BGButtonData());
	}

	public GUIData(String guiName) {
		this.guiFile = new File(KageCore.getPluginDirectory() + "/gui/" + guiName + ".gd");
		this.load();
	}

	public void load() {
		if(!this.deleted) {
			try {
				if(!this.guiFile.exists()) {
					this.guiFile.getParentFile().mkdirs();
					this.guiFile.createNewFile();
					this.save();
				}

				DocumentBuilderFactory ex = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = ex.newDocumentBuilder();
				Document document = documentBuilder.parse(this.guiFile);
				Element root = document.getDocumentElement();
				root.normalize();
				this.title = root.getAttribute("title");
				this.height = Integer.parseInt(root.getAttribute("size"));

				for(String component : allComponents.keySet()) {
					NodeList componentNodeList = document.getElementsByTagName(component);

					for(int i = 0; i < componentNodeList.getLength(); ++i) {
						Node node = componentNodeList.item(i);

						if(node.getNodeType() == 1) {
							Element element = (Element) node;
							this.components.add(((BGComponentData) allComponents.get(component)).parseXML(element));
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public void save() {
		if(!this.deleted) {
			try {
				if(!this.guiFile.exists()) {
					this.guiFile.createNewFile();
				}

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();
				Element root = document.createElement("gui");
				document.appendChild(root);
				root.setAttribute("title", this.title);
				root.setAttribute("size", "" + this.height);

				for(BGComponentData component : components) {
					Element domSource = document.createElement(component.getComponentName());
					component.saveToXML(domSource);
					root.appendChild(domSource);
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(this.guiFile);
				transformer.setOutputProperty("indent", "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(domSource, streamResult);
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public void delete() {
		this.setDeleted(true);
		this.guiFile.delete();
	}

	public BGGUI toGUI() {
		BGGUI gui = new BGGUI();
		gui.setTitle(this.title);
		gui.setHeight(this.height);

		for(BGComponentData component : components) {
			gui.add(component.toBGComponent());
		}

		return gui;
	}
}
