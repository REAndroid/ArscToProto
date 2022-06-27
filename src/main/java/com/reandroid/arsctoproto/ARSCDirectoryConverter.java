package com.reandroid.arsctoproto;

import com.android.aapt.ResourceTable;
import com.android.aapt.XmlNode;
import com.google.protobuf.GeneratedMessageV3;
import com.reandroid.arsctoproto.converters.ProtoTable;
import com.reandroid.arsctoproto.converters.xml.ProtoXmlDocument;
import com.reandroid.lib.arsc.chunk.xml.AndroidManifestBlock;
import com.reandroid.lib.arsc.io.BlockReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ARSCDirectoryConverter {
    private final File mArscDirectory;
    private final File mOutDirectory;
    private DirectoryResourceChecker mResourceFileChecker;
    private File mManifestFile;
    private File mResourcesFile;
    private String mOutSubDirectoryName;
    private File mOutSubDirectory;

    public ARSCDirectoryConverter(File arscDirectory, File outDirectory){
        this.mArscDirectory=arscDirectory;
        this.mOutDirectory=outDirectory;
        this.mOutSubDirectoryName="base";
    }
    public void setOutSubDirectoryName(String name) {
        this.mOutSubDirectoryName = name;
        mOutSubDirectory=null;
    }

    public void start() throws IOException {
        File dir=mArscDirectory;
        if(!dir.isDirectory()){
            throw new IOException("No such directory: "+dir.getAbsolutePath());
        }
        File[] subDirs=dir.listFiles();
        if(subDirs==null || subDirs.length==0){
            throw new IOException("Empty directory: "+dir.getAbsolutePath());
        }
        convertManifest();
        convertResources();
        convertResourceXml();
        convertResourceOtherFiles();
        copyDexFiles();
        convertAssetsFiles();
        convertRootFiles();
    }
    private void convertManifest() throws IOException {
        File manifestFile=getManifestFile();
        if(!manifestFile.isFile()){
            throw new IOException("Missing manifest file: "+manifestFile.getAbsolutePath());
        }
        AndroidManifestBlock resXmlBlock=new AndroidManifestBlock();
        InputStream inputStream=new FileInputStream(manifestFile);
        BlockReader reader=new BlockReader(inputStream);
        resXmlBlock.readBytes(reader);
        resXmlBlock.setPackageName("com.blabla.app");
        String junk=resXmlBlock.toString();
        System.out.println(junk);
        XmlNode.Builder xmlNodeBuilder = ProtoXmlDocument.toXmlNode(resXmlBlock, getResourceFileChecker());
        XmlNode xmlNode=xmlNodeBuilder.build();

        File outFile=getManifestOutFile();
        write(outFile, xmlNode);
    }
    private void convertResources() throws IOException {
        File resourcesFile=getResourcesFile();
        if(!resourcesFile.isFile()){
            throw new IOException("Missing resources file: "+resourcesFile.getAbsolutePath());
        }

        File outFile=getResourcesOutFile();
        ResourceTable.Builder resourceTable = ProtoTable.toResourceTable(resourcesFile, getResourceFileChecker());
        ResourceTable table = resourceTable.build();
        write(outFile, table);
    }
    private void convertAssetsFiles() throws IOException {
        List<File> fileList=recursiveFiles(getAssetsInDir());
        for(File file:fileList){
            convertAssetsFile(file);
        }
    }
    private void convertAssetsFile(File file) throws IOException {
        File outFile=toAssetsOutFile(file);
        if(outFile.isFile()){
            return;
        }
        copyFile(file, outFile);
    }
    private void convertRootFiles() throws IOException {
        List<File> fileList=recursiveFiles(mArscDirectory);
        for(File file:fileList){
            convertRootFiles(file);
        }
    }
    private void convertRootFiles(File file) throws IOException {
        if(!isRootFile(file)){
            return;
        }
        File outFile=toRootOutFile(file);
        if(outFile.isFile()){
            return;
        }
        copyFile(file, outFile);
    }
    private boolean isRootFile(File file){
        if(file.equals(getManifestFile())){
            return false;
        }
        if(file.equals(getResourcesFile())){
            return false;
        }
        if(isDexFile(file)){
            return false;
        }
        if(isAssetsFile(file)){
            return false;
        }
        DirectoryResourceChecker resourceChecker=getResourceFileChecker();
        if(resourceChecker.contains(file)){
            return false;
        }
        return true;
    }
    private void convertResourceXml() throws IOException {
        Set<File> xmlFileList=getResourceFileChecker().getResXmlFiles();
        for(File file:xmlFileList){
            convertResourceXml(file);
        }
    }
    private void convertResourceOtherFiles() throws IOException {
        Set<File> otherFileList=getResourceFileChecker().getResOtherFiles();
        for(File file:otherFileList){
            convertResourceOtherFiles(file);
        }
    }
    private void copyDexFiles() throws IOException {
        File[] files=mArscDirectory.listFiles();
        for(File file:files){
            if(!isDexFile(file)){
                continue;
            }
            copyDexFile(file);
        }
    }
    private boolean isDexFile(File file){
        if(!file.isFile()){
            return false;
        }
        File dir=file.getParentFile();
        if(!dir.equals(mArscDirectory)){
            return false;
        }
        String name=file.getName();
        if(!name.endsWith(".dex")){
            return false;
        }
        return true;
    }
    private boolean isAssetsFile(File file){
        File dir=getAssetsInDir();
        String path=dir.getAbsolutePath()+File.separator;
        return file.getAbsolutePath().startsWith(path);
    }
    private File getAssetsInDir(){
        File dir=new File(mArscDirectory, "assets");
        return dir;
    }
    private void copyDexFile(File dexFile) throws IOException {
        File outFile=new File(getOutSubDirectory(), "dex");
        outFile=new File(outFile, dexFile.getName());
        if(outFile.isFile()){
            return;
        }
        copyFile(dexFile, outFile);
    }

    private void convertResourceOtherFiles(File file) throws IOException {
        File outFile=toResOutFile(file);
        if(outFile.isFile()){
            return;
        }
        copyFile(file, outFile);
    }
    private void copyFile(File inFile, File outFile) throws IOException {
        File dir=outFile.getParentFile();
        if(!dir.isDirectory()){
            dir.mkdirs();
        }
        FileInputStream inputStream=new FileInputStream(inFile);
        FileOutputStream outputStream=new FileOutputStream(outFile);
        int size=2048;
        byte[] buffer=new byte[size];
        int len;
        while ((len=inputStream.read(buffer))>0){
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }
    private void convertResourceXml(File xmlFile) throws IOException {
        XmlNode.Builder xmlNodeBuilder = ProtoXmlDocument.toXmlNode(xmlFile, getResourceFileChecker());
        XmlNode xmlNode=xmlNodeBuilder.build();
        File outFile=toResOutFile(xmlFile);
        write(outFile, xmlNode);
    }
    private File toResOutFile(File inFile){

        String path=mArscDirectory.getAbsolutePath()+File.separator;
        String relPath=inFile.getAbsolutePath();
        relPath=relPath.substring(path.length());
        File file=new File(getOutSubDirectory(), relPath);
        return file;
    }
    private File toRootOutFile(File inFile){
        String path=mArscDirectory.getAbsolutePath()+File.separator;
        String relPath=inFile.getAbsolutePath();
        relPath=relPath.substring(path.length());
        File file=new File(getOutSubDirectory(), "root");
        file=new File(file, relPath);
        return file;
    }
    private File toAssetsOutFile(File inFile){
        String path=mArscDirectory.getAbsolutePath()+File.separator;
        String relPath=inFile.getAbsolutePath();
        relPath=relPath.substring(path.length());
        File file=new File(getOutSubDirectory(), relPath);
        return file;
    }

    private void write(File file, GeneratedMessageV3 proto) throws IOException{
        File dir=file.getParentFile();
        if(!dir.exists()){
            dir.mkdirs();
        }
        OutputStream outputStream=new FileOutputStream(file);
        proto.writeTo(outputStream);
    }
    private File getResourcesOutFile(){
        File dir=getOutSubDirectory();
        File file=new File(dir, RES_PB_FILE_NAME);
        return file;
    }
    private File getManifestOutFile(){
        File dir=getOutSubDirectory();
        dir=new File(dir, MANIFEST_DIR_NAME);
        File file=new File(dir, MANIFEST_FILE_NAME);
        return file;
    }
    private File getOutSubDirectory(){
        if(mOutSubDirectory==null){
            mOutSubDirectory=new File(mOutDirectory, mOutSubDirectoryName);
        }
        return mOutSubDirectory;
    }
    private File getManifestFile(){
        if(mManifestFile==null){
            mManifestFile=new File(mArscDirectory, MANIFEST_FILE_NAME);
        }
        return mManifestFile;
    }
    private File getResourcesFile(){
        if(mResourcesFile==null){
            mResourcesFile=new File(mArscDirectory, RES_ARSC_FILE_NAME);
        }
        return mResourcesFile;
    }
    public DirectoryResourceChecker getResourceFileChecker() {
        if(mResourceFileChecker==null){
            mResourceFileChecker=new DirectoryResourceChecker(mArscDirectory);
        }
        return mResourceFileChecker;
    }
    public void setResourceFileChecker(DirectoryResourceChecker resourceFileChecker) {
        this.mResourceFileChecker = resourceFileChecker;
    }
    private static List<File> recursiveFiles(File dir){
        List<File> results=new ArrayList<>();
        if(!dir.isDirectory()){
            results.add(dir);
            return results;
        }
        File[] files=dir.listFiles();
        if(files==null){
            return results;
        }
        for(File file:files){
            if(file.isFile()){
                results.add(file);
            }else {
                results.addAll(recursiveFiles(file));
            }
        }
        return results;
    }

    private static final String MANIFEST_FILE_NAME="AndroidManifest.xml";
    private static final String MANIFEST_DIR_NAME="manifest";
    private static final String RES_ARSC_FILE_NAME="resources.arsc";
    private static final String RES_PB_FILE_NAME="resources.pb";
}
