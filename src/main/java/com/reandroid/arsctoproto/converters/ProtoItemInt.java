package com.reandroid.arsctoproto.converters;

import com.android.aapt.Item;
import com.reandroid.lib.arsc.chunk.TableBlock;
import com.reandroid.lib.arsc.item.TableString;
import com.reandroid.lib.arsc.pool.TableStringPool;
import com.reandroid.lib.arsc.value.ResValueInt;
import com.reandroid.lib.arsc.value.ValueType;

public class ProtoItemInt extends ProtoItemBase<ProtoValue, ResValueInt>{
    private Item.Builder mBuilder;
    public ProtoItemInt(){
        super();
    }
    @Override
    public void convertARSC(ProtoValue parent, ResValueInt block) {
        mBuilder=loadItem(block);
    }
    Item.Builder getItemBuilder(){
        return mBuilder;
    }
    private Item.Builder loadItem(ResValueInt block){
        Item.Builder itemBuilder=loadId(block);
        if(itemBuilder!=null){
            return itemBuilder;
        }
        ValueType vt = block.getValueType();
        if(vt==ValueType.STRING){
            return loadString(block);
        }
        itemBuilder=ProtoItemUtil.toItemBuilder(vt, block.getData());
        if(itemBuilder!=null){
            return itemBuilder;
        }
        return null;
    }
    private Item.Builder loadId(ResValueInt block){
        if(!isId(block)){
            return null;
        }
        Item.Builder itemBuilder= com.android.aapt.Item.newBuilder();
        com.android.aapt.Id.Builder builder= com.android.aapt.Id.newBuilder();
        itemBuilder.setId(builder);
        return itemBuilder;
    }
    private Item.Builder loadString(ResValueInt block){
        TableBlock tableBlock = block.getEntryBlock().getPackageBlock().getTableBlock();
        int raw=block.getData();
        TableStringPool pool = tableBlock.getTableStringPool();
        TableString tableString = pool.get(raw);
        String value=tableString.getHtml();
        Item.Builder itemBuilder= convertStringValue(value);
        return itemBuilder;
    }
}
