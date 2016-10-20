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
		guiFile = new File(KageCore.getPluginDirectory() + "/gui/" + guiName + ".gd");
		load();
	}

	public void load() {
		if(!deleted) {
			try {
				if(!guiFile.exists()) {
					guiFile.getParentFile().mkdirs();
					guiFile.createNewFile();
					save();
				}

				DocumentBuilderFactory ex = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = ex.newDocumentBuilder();
				Document document = documentBuilder.parse(guiFile);
				Element root = document.getDocumentElement();
				root.normalize();
				title = root.getAttribute("title");
				height = Integer.parseInt(root.getAttribute("size"));

				for(String component : allComponents.keySet()) {
					NodeList componentNodeList = document.getElementsByTagName(component);

					for(int i = 0; i < componentNodeList.getLength(); ++i) {
						Node node = componentNodeList.item(i);

						if(node.getNodeType() == 1) {
							Element element = (Element) node;
							components.add(((BGComponentData) allComponents.get(component)).parseXML(element));
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public void save() {
		if(!deleted) {
			try {
				if(!guiFile.exists()) {
					guiFile.createNewFile();
				}

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();
				Element root = document.createElement("gui");
				document.appendChild(root);
				root.setAttribute("title", title);
				root.setAttribute("size", "" + height);

				for(BGComponentData component : components) {
					Element domSource = document.createElement(component.getComponentName());
					component.saveToXML(domSource);
					root.appendChild(domSource);
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(guiFile);
				transformer.setOutputProperty("indent", "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(domSource, streamResult);
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public void delete() {
		setDeleted(true);
		guiFile.delete();
	}

	public BGGUI toGUI() {
		BGGUI gui = new BGGUI();
		gui.setTitle(title);
		gui.setHeight(height);

		for(BGComponentData component : components) {
			gui.add(component.toBGComponent());
		}

		return gui;
	}
}
