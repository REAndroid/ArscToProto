package com.reandroid.arsctoproto.converters.xml;

import com.android.aapt.*;
import com.reandroid.lib.arsc.chunk.xml.*;

import java.util.Collection;
import java.util.List;

public class ProtoXmlElement extends ProtoXmlBase<ProtoXmlElement, ResXmlElement> {
    private XmlElement.Builder mBuilder;
    public ProtoXmlElement(){
        super();
    }
    @Override
    public void convertARSC(ProtoXmlElement ignored, ResXmlElement resXmlElement) {
        mBuilder=null;
        XmlElement.Builder xmlElementBuilder=getXmlElementBuilder();

        xmlElementBuilder.setName(resXmlElement.getTag());
        String tagUri=resXmlElement.getTagUri();
        if(tagUri!=null){
            xmlElementBuilder.setNamespaceUri(tagUri);
        }

        addNamespaceDeclarations(xmlElementBuilder, resXmlElement);
        addAllAttributes(xmlElementBuilder, resXmlElement);
        addChildes(xmlElementBuilder, resXmlElement);
    }
    private void addNamespaceDeclarations(XmlElement.Builder builder, ResXmlElement resXmlElement){
        List<ResXmlStartNamespace> namespaceList = resXmlElement.getStartNamespaceList();
        for(ResXmlStartNamespace startNamespace:namespaceList){

            XmlNamespace.Builder xmlNamespaceBuilder=XmlNamespace.newBuilder();
            xmlNamespaceBuilder.setUri(startNamespace.getUri());
            xmlNamespaceBuilder.setPrefix(startNamespace.getPrefix());

            SourcePosition.Builder sourcePositionBuilder = xmlNamespaceBuilder.getSourceBuilder();
            sourcePositionBuilder.setLineNumber(startNamespace.getLineNumber());

            builder.addNamespaceDeclaration(xmlNamespaceBuilder);
        }
    }
    private void addChildes(XmlElement.Builder builder, ResXmlElement resXmlElement){
        List<ResXmlElement> childElementList = resXmlElement.listElements();
        for(ResXmlElement child:childElementList){
            ProtoXmlElement protoXmlElement=new ProtoXmlElement();
            protoXmlElement.setResourceFileChecker(getResourceFileChecker());

            protoXmlElement.convertARSC(this, child);

            XmlElement.Builder childBuilder= protoXmlElement.getXmlElementBuilder();

            XmlNode.Builder childNode = builder.addChildBuilder();

            setText(childNode, resXmlElement);

            addSourcePosition(childNode, child);

            childNode.setElement(childBuilder);
        }
    }
    private void addAllAttributes(XmlElement.Builder builder, ResXmlElement block){
        Collection<ResXmlAttribute> blockAttrs = block.listResXmlAttributes();
        for(ResXmlAttribute resXmlAttribute:blockAttrs){
            ProtoXmlAttribute protoXmlAttribute=new ProtoXmlAttribute();
            protoXmlAttribute.setResourceFileChecker(getResourceFileChecker());

            protoXmlAttribute.convertARSC(this, resXmlAttribute);
            XmlAttribute.Builder attrBuilder = protoXmlAttribute.getXmlAttributeBuilder();
            builder.addAttribute(attrBuilder);
        }
    }
    public XmlElement.Builder getXmlElementBuilder(){
        if(mBuilder==null){
            mBuilder=XmlElement.newBuilder();
        }
        return mBuilder;
    }
}
