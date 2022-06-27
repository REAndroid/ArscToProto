package com.reandroid.arsctoproto.converters.xml;

import com.android.aapt.SourcePosition;
import com.android.aapt.XmlNode;
import com.reandroid.arsctoproto.converters.ProtoBase;
import com.reandroid.lib.arsc.base.Block;
import com.reandroid.lib.arsc.chunk.xml.ResXmlElement;
import com.reandroid.lib.arsc.chunk.xml.ResXmlStartElement;
import com.reandroid.lib.arsc.chunk.xml.ResXmlText;

abstract class ProtoXmlBase<PARENT extends ProtoXmlBase, ARSC extends Block> extends ProtoBase<PARENT, ARSC> {

    public ProtoXmlBase(){
        super();
    }
    void addSourcePosition(XmlNode.Builder xmlNodeBuilder, ResXmlElement resXmlElement){
        ResXmlStartElement startElement = resXmlElement.getStartElement();
        if(startElement==null){
            return;
        }
        SourcePosition.Builder sourcePositionBuilder = xmlNodeBuilder.getSourceBuilder();
        sourcePositionBuilder.setLineNumber(startElement.getLineNumber());
    }

     void setText(XmlNode.Builder xmlNodeBuilder, ResXmlElement resXmlElement){
        ResXmlText resXmlText= resXmlElement.getResXmlText();
        if(resXmlText==null){
            return;
        }
        xmlNodeBuilder.setText(resXmlText.getText());
    }
}
