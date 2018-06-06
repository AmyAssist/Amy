/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is responsible for parsing poms
 * 
 * @author Tim Neumann
 */
public class PomParser {

	/**
	 * The parsed document
	 */
	private Document document;

	/**
	 * Creates a new Pom Parser for the given file
	 * 
	 * @param pom
	 *            The inputStream for the pom to parse
	 * @throws IOException
	 *             If any IO errors occur.
	 * @throws SAXException
	 *             If any parse errors occur.
	 * @throws IllegalStateException
	 *             On internal error
	 */
	public PomParser(InputStream pom) throws IOException, SAXException, IllegalStateException {
		try {
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IllegalStateException("Internal Error:" + e.getLocalizedMessage());
		}

		this.document.getDocumentElement().normalize();
		// this.logXML(this.getRoot(), "");
	}

	/**
	 * @return the name of the artifact
	 */
	public String getName() {
		return this.getFirstChildNodeWithName(this.getRoot(), "name").getFirstChild().getNodeValue();
	}

	/**
	 * @return the id of the artifact
	 */
	public String getId() {
		Node artifactNode = this.getFirstChildNodeWithName(this.getRoot(), "artifactId");
		if (artifactNode == null)
			throw new IllegalStateException("No artifact node...");
		return artifactNode.getFirstChild().getNodeValue();
	}

	/**
	 * @return the group id of the artifact
	 */
	public String getGroupId() {
		Node groupNode = this.getFirstChildNodeWithName(this.getRoot(), "groupId");
		if (groupNode == null) {
			Node parentNode = this.getFirstChildNodeWithName(this.getRoot(), "parent");
			if (parentNode == null)
				throw new IllegalStateException("No group node and no parent node...");
			groupNode = this.getFirstChildNodeWithName(parentNode, "groupId");
		}
		if (groupNode == null)
			throw new IllegalStateException("No group node and no group node in parent node...");

		return groupNode.getFirstChild().getNodeValue();
	}

	/**
	 * @return the version of the artifact
	 */
	public String getVersion() {
		Node versionNode = this.getFirstChildNodeWithName(this.getRoot(), "version");
		if (versionNode == null) {
			Node parentNode = this.getFirstChildNodeWithName(this.getRoot(), "parent");
			if (parentNode == null)
				throw new IllegalStateException("No version node and no parent node...");
			versionNode = this.getFirstChildNodeWithName(parentNode, "version");
		}
		if (versionNode == null)
			throw new IllegalStateException("No version node and no version node in parent node...");

		return versionNode.getFirstChild().getNodeValue();
	}

	/**
	 * @return {@link #getGroupId() groupId} + "." + {@link #getId() artifactId}
	 */
	public String getFullId() {
		return this.getGroupId() + "." + this.getId();
	}

	/**
	 * Get's the dependencys of this pom.
	 * 
	 * @return the fullIds of the dependency.
	 */
	public String[] getDependencyIds() {
		Node dependencies = this.getFirstChildNodeWithName(this.getRoot(), "dependencies");
		if (dependencies == null)
			return new String[0];
		ArrayList<String> res = new ArrayList<>();
		for (int i = 0; i < dependencies.getChildNodes().getLength(); i++) {
			Node dep = dependencies.getChildNodes().item(i);
			if (dep == null || dep.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			String artifactId = this.getFirstChildNodeWithName(dep, "artifactId").getFirstChild().getNodeValue();
			String groupId = this.getFirstChildNodeWithName(dep, "groupId").getFirstChild().getNodeValue();
			res.add(groupId + "." + artifactId);
		}
		return res.toArray(new String[0]);
	}

	/**
	 * Get's the root element of the dom (called project)
	 * 
	 * @return The root node
	 */
	private Node getRoot() {
		return this.document.getDocumentElement();
	}

	/**
	 * Get's the first child node with the given name. Only searches in the
	 * direct childs, and does not go deeper into the tree.
	 * 
	 * @param name
	 *            The name of the node to search
	 * @param node
	 *            The parent node to search in
	 * @return The first child node found or null if none was found
	 */
	private Node getFirstChildNodeWithName(Node node, String name) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals(name))
				return nodes.item(i);
		}
		return null;
	}

	/**
	 * Logs the complete XML to sysout
	 */
	public void logXML() {
		this.logXML(this.getRoot(), "");
	}

	private void logXML(Node node, String prefix) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			System.out.println(prefix + "N:" + node.getNodeName());
			System.out.println(prefix + "    " + this.getNodeAttributeString(node));
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				this.logXML(node.getChildNodes().item(i), prefix + "-");
			}
		} else if (node.getNodeType() == Node.TEXT_NODE) {
			String text = node.getNodeValue();
			if (!text.equals("")) {
				System.out.println(prefix + "    T:" + text);
			}
		}
	}

	private String getNodeAttributeString(Node node) {

		if (node.getAttributes() == null || node.getAttributes().getLength() < 1)
			return "()";

		String res = "(";

		for (int i = 0; i < node.getAttributes().getLength(); i++) {
			res += node.getAttributes().item(i).getNodeName() + ":" + node.getAttributes().item(i).getNodeValue()
					+ ", ";
		}
		res = res.substring(0, res.length() - 2);
		res += ")";
		return res;
	}
}
