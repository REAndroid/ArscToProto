package com.reandroid.arsctoproto.converters.xml;

import com.android.aapt.XmlElement;
import com.android.aapt.XmlNode;
import com.reandroid.arsctoproto.converters.ResourceFileChecker;
import com.reandroid.lib.arsc.chunk.ChunkType;
import com.reandroid.lib.arsc.chunk.xml.ResXmlBlock;
import com.reandroid.lib.arsc.chunk.xml.ResXmlElement;
import com.reandroid.lib.arsc.header.HeaderBlock;
import com.reandroid.lib.arsc.io.BlockReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProtoXmlDocument extends ProtoXmlBase<ProtoXmlDocument, ResXmlBlock> {
    private XmlNode.Builder mBuilder;
    public ProtoXmlDocument(){
        super();
    }
    @Override
    public void convertARSC(ProtoXmlDocument ignored, ResXmlBlock block) {
        mBuilder=null;
        XmlNode.Builder xmlNodeBuilder=getXmlNodeBuilder();

        ResXmlElement resXmlElement = block.getResXmlElement();
        ProtoXmlElement protoXmlElement=new ProtoXmlElement();
        protoXmlElement.setResourceFileChecker(getResourceFileChecker());
        protoXmlElement.convertARSC(protoXmlElement, resXmlElement);

        addSourcePosition(xmlNodeBuilder, resXmlElement);

        XmlElement.Builder mlElementBuilder= protoXmlElement.getXmlElementBuilder();
        setText(xmlNodeBuilder, resXmlElement);

        xmlNodeBuilder.setElement(mlElementBuilder);
    }
    public XmlNode.Builder getXmlNodeBuilder(){
        if(mBuilder==null){
            mBuilder=XmlNode.newBuilder();
        }
        return mBuilder;
    }

    public static XmlNode.Builder toXmlNode(File resXmlFile, ResourceFileChecker resourceChecker) throws IOException {
        InputStream inputStream=new FileInputStream(resXmlFile);
        return toXmlNode(inputStream, resourceChecker);
    }
    public static XmlNode.Builder toXmlNode(InputStream inputStream, ResourceFileChecker resourceChecker) throws IOException {
        BlockReader reader=new BlockReader(inputStream);
        HeaderBlock header = reader.readHeaderBlock();
        if(header==null){
            throw new IOException("Null header block");
        }
        ChunkType chunkType = header.getChunkType();

        if(chunkType!=ChunkType.XML){
            throw new IOException("Invalid XML header chunk type: "+header.toString());
        }
        ResXmlBlock resXmlBlock=new ResXmlBlock();
        resXmlBlock.readBytes(reader);

        return toXmlNode(resXmlBlock, resourceChecker);
    }

    public static XmlNode.Builder toXmlNode(ResXmlBlock resXmlBlock, ResourceFileChecker resourceChecker){
        ProtoXmlDocument protoXmlDocument=new ProtoXmlDocument();
        protoXmlDocument.setResourceFileChecker(resourceChecker);
        protoXmlDocument.convertARSC(protoXmlDocument, resXmlBlock);
        XmlNode.Builder builder= protoXmlDocument.getXmlNodeBuilder();
        return builder;
    }
}
