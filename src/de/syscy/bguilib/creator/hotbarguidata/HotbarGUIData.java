package de.syscy.bguilib.creator.hotbarguidata;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

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

import de.syscy.bguilib.BGHotbarGUI;
import de.syscy.kagecore.KageCore;
import lombok.Getter;
import lombok.Setter;

public class HotbarGUIData {

	private static Map<String, BGHotbarComponentData> allComponents = new HashMap<>();
	private @Getter File guiFile;
	private @Getter BGHotbarComponentData[] components = new BGHotbarComponentData[9];
	private @Getter @Setter boolean deleted = false;

	static {
		allComponents.put(BGHotbarButtonData.componentName, new BGHotbarButtonData());
	}

	public HotbarGUIData(String guiName) {
		guiFile = new File(KageCore.getPluginDirectory() + "/gui/" + guiName + ".hgd");
		load();
	}

	public void load() {
		if(!deleted) {
			try {
				if(!guiFile.exists()) {
					guiFile.mkdirs();
					guiFile.createNewFile();
					save();
				}

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(guiFile);
				Element root = document.getDocumentElement();
				root.normalize();

				for(String component : allComponents.keySet()) {
					NodeList componentNodeList = document.getElementsByTagName(component);

					for(int i = 0; i < componentNodeList.getLength(); ++i) {
						Node node = componentNodeList.item(i);

						if(node.getNodeType() == 1) {
							Element element = (Element) node;
							BGHotbarComponentData newComponent = ((BGHotbarComponentData) allComponents.get(component)).parseXML(element);
							components[newComponent.getX()] = newComponent;
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
				FileOutputStream fileOutputStream = new FileOutputStream(guiFile);

				try {
					if(!guiFile.exists()) {
						guiFile.createNewFile();
					}

					DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
					Document document = documentBuilder.newDocument();
					Element root = document.createElement("hgui");
					document.appendChild(root);
					BGHotbarComponentData[] streamResult = components;
					int domSource = components.length;

					for(int transformer = 0; transformer < domSource; ++transformer) {
						BGHotbarComponentData transformerFactory = streamResult[transformer];
						if(transformerFactory != null) {
							Element componentElement = document.createElement(transformerFactory.getComponentName());
							transformerFactory.saveToXML(componentElement);
							root.appendChild(componentElement);
						}
					}

					TransformerFactory var22 = TransformerFactory.newInstance();
					Transformer var23 = var22.newTransformer();
					DOMSource var24 = new DOMSource(document);
					StreamResult var25 = new StreamResult(guiFile);
					var23.setOutputProperty("indent", "yes");
					var23.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
					var23.transform(var24, var25);
				} finally {
					if(fileOutputStream != null) {
						fileOutputStream.close();
					}

				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public void delete() {
		setDeleted(true);
		guiFile.delete();
	}

	public BGHotbarGUI toHGUI() {
		BGHotbarGUI gui = new BGHotbarGUI();

		for(BGHotbarComponentData component : components) {
			if(component != null) {
				gui.add(component.toBGHotbarComponent());
			}
		}

		return gui;
	}
}
