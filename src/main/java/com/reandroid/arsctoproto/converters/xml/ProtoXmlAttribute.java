package com.reandroid.arsctoproto.converters.xml;

import com.android.aapt.Item;
import com.android.aapt.PBString;
import com.android.aapt.XmlAttribute;

import com.reandroid.arsctoproto.converters.ProtoItemUtil;
import com.reandroid.lib.arsc.chunk.xml.ResXmlAttribute;
import com.reandroid.lib.arsc.chunk.xml.ResXmlStartNamespace;
import com.reandroid.lib.arsc.value.ValueType;

public class ProtoXmlAttribute extends ProtoXmlBase<ProtoXmlElement, ResXmlAttribute> {
    public ProtoXmlAttribute(){
        super();
    }
    private XmlAttribute.Builder mBuilder;
    @Override
    public void convertARSC(ProtoXmlElement ignored, ResXmlAttribute resXmlAttribute) {
        XmlAttribute.Builder xmlAttributeBuilder=getXmlAttributeBuilder();
        String name=resXmlAttribute.getName();
        if(name!=null){
            xmlAttributeBuilder.setName(name);
        }
        updateNameSpace(xmlAttributeBuilder, resXmlAttribute.getStartNamespace());
        xmlAttributeBuilder.setResourceId(resXmlAttribute.getNameResourceID());
        ValueType valueType=resXmlAttribute.getValueType();
        Item.Builder itemBuilder;
        if(valueType==ValueType.STRING){
            itemBuilder=loadStringValue(resXmlAttribute);
        }else {
            itemBuilder=ProtoItemUtil.toItemBuilder(valueType, resXmlAttribute.getRawValue());
        }
        xmlAttributeBuilder.setCompiledItem(itemBuilder);
    }
    private void updateNameSpace(XmlAttribute.Builder xmlAttributeBuilder, ResXmlStartNamespace startNamespace){
        if(startNamespace==null){
            return;
        }
        String uri=startNamespace.getUri();
        xmlAttributeBuilder.setNamespaceUri(uri);
    }

    private Item.Builder loadStringValue(ResXmlAttribute resXmlAttribute){
        String value=resXmlAttribute.getValueString();
        Item.Builder itemBuilder= convertStringValue(value);

        PBString.Builder strBuilder= PBString.newBuilder();
        strBuilder.setValue(value);
        itemBuilder.setStr(strBuilder);

        return itemBuilder;
    }
    public XmlAttribute.Builder getXmlAttributeBuilder(){
        if(mBuilder==null){
            mBuilder=XmlAttribute.newBuilder();
        }
        return mBuilder;
    }
}
