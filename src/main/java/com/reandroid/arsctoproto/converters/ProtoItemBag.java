package com.reandroid.arsctoproto.converters;

import com.android.aapt.*;
import com.reandroid.lib.arsc.chunk.TableBlock;
import com.reandroid.lib.arsc.item.TableString;
import com.reandroid.lib.arsc.pool.TableStringPool;
import com.reandroid.lib.arsc.value.ResValueBag;
import com.reandroid.lib.arsc.value.ResValueBagItem;
import com.reandroid.lib.arsc.value.ValueType;
import com.reandroid.lib.arsc.value.attribute.AttributeBag;
import com.reandroid.lib.arsc.value.attribute.AttributeBagItem;
import com.reandroid.lib.arsc.value.attribute.AttributeValueType;
import com.reandroid.lib.arsc.value.plurals.PluralsQuantity;

public class ProtoItemBag extends ProtoItemBase<ProtoValue, ResValueBag>{
    private com.android.aapt.CompoundValue.Builder mBuilder;
    public ProtoItemBag(){
        super();
    }
    @Override
    public void convertARSC(ProtoValue ignored, ResValueBag block) {
        mBuilder=loadBag(block);
    }
    private com.android.aapt.CompoundValue.Builder loadBag(ResValueBag block){
        if(isAttr(block)){
            return loadAttr(block);
        }
        if(isArray(block)){
            return loadArray(block);
        }
        if(isPlural(block)){
            return loadPlurals(block);
        }
        if(isStyle(block)){
            return loadStyle(block);
        }
        return null;
    }
    private com.android.aapt.CompoundValue.Builder loadStyle(ResValueBag block){
        com.android.aapt.CompoundValue.Builder compoundBuilder=com.android.aapt.CompoundValue.newBuilder();
        com.android.aapt.Style.Builder builder=com.android.aapt.Style.newBuilder();
        int parentId=block.getParentId();
        if(parentId!=0){
            com.android.aapt.Reference.Builder refBuilder= com.android.aapt.Reference.newBuilder();
            refBuilder.setId(parentId);
            builder.setParent(refBuilder);
        }
        ResValueBagItem[] allItems = block.getBagItems();
        for(int i=0;i<allItems.length;i++){
            ResValueBagItem bagItem=allItems[i];

            Style.Entry.Builder entryBuilder=Style.Entry.newBuilder();
            Item.Builder itemBuilder=loadItem(bagItem);
            if(itemBuilder==null){
                continue;
            }

            Reference.Builder refKey=Reference.newBuilder();
            refKey.setId(bagItem.getId());


            entryBuilder.setKey(refKey);
            entryBuilder.setItem(itemBuilder);

            builder.addEntry(entryBuilder);
        }

        compoundBuilder.setStyle(builder);
        return compoundBuilder;
    }
    private com.android.aapt.CompoundValue.Builder loadPlurals(ResValueBag block){
        com.android.aapt.CompoundValue.Builder compoundBuilder=com.android.aapt.CompoundValue.newBuilder();
        com.android.aapt.Plural.Builder builder=com.android.aapt.Plural.newBuilder();
        ResValueBagItem[] allItems = block.getBagItems();
        for(int i=0;i<allItems.length;i++){
            ResValueBagItem bagItem=allItems[i];
            PluralsQuantity quantity = PluralsQuantity.valueOf(bagItem.getIdLow());

            Plural.Entry.Builder entryBuilder=Plural.Entry.newBuilder();
            Item.Builder itemBuilder=loadItem(bagItem);
            if(itemBuilder==null){
                continue;
            }
            switch (quantity){
                case OTHER:
                    entryBuilder.setArity(Plural.Arity.OTHER);
                    break;
                case ZERO:
                    entryBuilder.setArity(Plural.Arity.ZERO);
                    break;
                case ONE:
                    entryBuilder.setArity(Plural.Arity.ONE);
                    break;
                case TWO:
                    entryBuilder.setArity(Plural.Arity.TWO);
                    break;
                case FEW:
                    entryBuilder.setArity(Plural.Arity.FEW);
                    break;
                case MANY:
                    entryBuilder.setArity(Plural.Arity.MANY);
                    break;
            }
            entryBuilder.setItem(itemBuilder);
            builder.addEntry(entryBuilder);
        }

        compoundBuilder.setPlural(builder);
        return compoundBuilder;
    }
    private com.android.aapt.CompoundValue.Builder loadArray(ResValueBag block){
        com.android.aapt.CompoundValue.Builder compoundBuilder=com.android.aapt.CompoundValue.newBuilder();
        com.android.aapt.PBArray.Builder builder=com.android.aapt.PBArray.newBuilder();
        ResValueBagItem[] allItems = block.getBagItems();
        for(int i=0;i<allItems.length;i++){
            ResValueBagItem bagItem=allItems[i];
            PBArray.Element.Builder elemBuilder=PBArray.Element.newBuilder();
            Item.Builder itemBuilder=loadItem(bagItem);
            if(itemBuilder==null){
                continue;
            }
            elemBuilder.setItem(itemBuilder);
            builder.addElement(elemBuilder);
        }

        compoundBuilder.setArray(builder);
        return compoundBuilder;
    }

    private com.android.aapt.Item.Builder loadItem(ResValueBagItem bagItem){
        com.android.aapt.Item.Builder itemBuilder;
        itemBuilder=loadPrimitive(bagItem);
        if(itemBuilder!=null){
            return itemBuilder;
        }
        ValueType vt = bagItem.getValueType();
        if(vt==ValueType.STRING){
            return loadString(bagItem);
        }
        if(vt==ValueType.REFERENCE){
            return loadReference(bagItem);
        }
        if(vt==ValueType.ATTRIBUTE){
            return loadReference(bagItem);
        }
        return null;
    }

    private com.android.aapt.Item.Builder loadPrimitive(ResValueBagItem bagItem){
        ValueType valueType=bagItem.getValueType();
        int raw=bagItem.getData();
        com.android.aapt.Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        com.android.aapt.Primitive.Builder builder= com.android.aapt.Primitive.newBuilder();
        if(valueType==ValueType.INT_BOOLEAN){
            boolean val= raw != 0;
            builder.setBooleanValue(val);
        }else if(valueType==ValueType.INT_COLOR_ARGB4){
            builder.setColorArgb4Value(raw);
        }else if(valueType==ValueType.INT_COLOR_ARGB8){
            builder.setColorArgb8Value(raw);
        }else if(valueType==ValueType.INT_COLOR_RGB4){
            builder.setColorRgb4Value(raw);
        }else if(valueType==ValueType.INT_COLOR_RGB8){
            builder.setColorRgb8Value(raw);
        }else if(valueType==ValueType.FIRST_COLOR_INT){
            builder.setColorArgb8Value(raw);
        }else if(valueType==ValueType.FLOAT){
            float val = Float.intBitsToFloat(raw);
            builder.setFloatValue(val);
        }else if(valueType==ValueType.FRACTION){
            builder.setFractionValue(raw);
        }else if(valueType==ValueType.DIMENSION){
            builder.setDimensionValue(raw);
        }else if(valueType==ValueType.INT_DEC){
            builder.setIntDecimalValue(raw);
        }else if(valueType==ValueType.INT_HEX){
            builder.setIntHexadecimalValue(raw);
        }else if(valueType==ValueType.FIRST_INT){
            builder.setIntDecimalValue(raw);
        }else{
            return null;
        }
        itemBuilder.setPrim(builder);
        return itemBuilder;
    }

    private com.android.aapt.Item.Builder loadReference(ResValueBagItem bagItem){
        int raw=bagItem.getData();
        com.android.aapt.Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        com.android.aapt.Reference.Builder refBuilder= com.android.aapt.Reference.newBuilder();
        refBuilder.setId(raw);
        itemBuilder.setRef(refBuilder);
        return itemBuilder;
    }
    private com.android.aapt.Item.Builder loadString(ResValueBagItem bagItem){
        TableBlock tableBlock = bagItem.getEntryBlock().getPackageBlock().getTableBlock();
        int raw=bagItem.getData();
        TableStringPool pool = tableBlock.getTableStringPool();
        TableString tableString = pool.get(raw);
        com.android.aapt.Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        String value=tableString.getHtml();
        if(value.startsWith("res/")){
            com.android.aapt.FileReference.Builder fileRefBuilder= com.android.aapt.FileReference.newBuilder();
            fileRefBuilder.setPath(value);
            if(value.endsWith(".xml")){
                fileRefBuilder.setType(com.android.aapt.FileReference.Type.PROTO_XML);
            }else if(value.endsWith(".png")){
                fileRefBuilder.setType(com.android.aapt.FileReference.Type.PNG);
            }
            itemBuilder.setFile(fileRefBuilder);
            log(value);
        }else {
            PBString.Builder strBuilder= PBString.newBuilder();
            strBuilder.setValue(value);
            itemBuilder.setStr(strBuilder);
        }


        return itemBuilder;
    }

    private com.android.aapt.CompoundValue.Builder loadAttr(ResValueBag block){
        com.android.aapt.CompoundValue.Builder compoundBuilder=com.android.aapt.CompoundValue.newBuilder();
        com.android.aapt.Attribute.Builder builder=com.android.aapt.Attribute.newBuilder();
        AttributeBag attributeBag=AttributeBag.create(block);
        AttributeBagItem[] bagItems = attributeBag.getBagItems();
        if(attributeBag.isEnum() ){
            for(int i=0;i<bagItems.length;i++){
                AttributeBagItem bagItem=bagItems[i];
                String name=bagItem.getName();
                if(name==null){
                    continue;
                }
                com.android.aapt.Attribute.Symbol.Builder symbolBuilder= com.android.aapt.Attribute.Symbol.newBuilder();
                com.android.aapt.Reference.Builder refBuilder= com.android.aapt.Reference.newBuilder();
                refBuilder.setId(bagItem.getBagItem().getId());
                refBuilder.setName("id/"+name);
                refBuilder.setTypeFlags(0xFFFF);
                refBuilder.setAllowRaw(true);
                symbolBuilder.setName(refBuilder);
                symbolBuilder.setValue(bagItem.getData());
                symbolBuilder.setType(0x10);
                builder.addSymbol(symbolBuilder);
            }
        }

        AttributeValueType[] valueTypes = attributeBag.getValueTypes();
        byte val=AttributeValueType.getByte(valueTypes);
        builder.setFormatFlags(val);

        attributeBag.getBagItems();
        compoundBuilder.setAttr(builder);
        return compoundBuilder;
    }

    com.android.aapt.CompoundValue.Builder getCompoundBuilder(){
        return mBuilder;
    }
}
