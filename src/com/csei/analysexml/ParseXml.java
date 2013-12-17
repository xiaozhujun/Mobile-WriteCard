package com.csei.analysexml;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
public class ParseXml {
	@SuppressWarnings("unchecked")
	public List<DbModel> parseRolesTable(String fileName){    //此方法主要在于将选中的RolesTablexml文件进行解析
		//根据不同的根节点来判断解析不同的文件
		SAXReader saxReader = new SAXReader();
		List<DbModel> list=new ArrayList<DbModel>();
        try {  
           Document document = saxReader.read(new File(fileName));  
           Element root = document.getRootElement();  
           List<Element> elements = root.elements();  
           Iterator<Element> it = elements.iterator();
           while(it.hasNext()) {  
               Element e = it.next();  
               System.out.println(e.getName() + " : " + e.attribute("name").getValue() + " -- " + e.attribute("roleNum").getValue());  
               List<Element> group = e.elements();  
               Iterator<Element> git = group.iterator();  
               //所有的过程其实就是层层解析的过程  
               while(git.hasNext()) {  
                   Element ge = git.next();  
                   //通过使用e.attribute(" ").getValue()获得属性的值  
                   System.out.println(ge.getName() + " : " + ge.attribute("name").getValue());
                   String tableitem=ge.attribute("name").getValue();
                   DbModel d=new DbModel();
                   d.setTableitem(tableitem);
                   list.add(d);
               }  
           }     
       } catch (DocumentException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
        return list;
   } 
	@SuppressWarnings({ "unchecked", "unused" })
	public int getEmployersCount(String fileName){
		int i=0;
		SAXReader saxReader = new SAXReader();
        try {  
           Document document = saxReader.read(new File(fileName));  
           Element root = document.getRootElement();  
           List<Element> elements = root.elements();  
           Iterator<Element> it = elements.iterator();  
           while(it.hasNext()) {  
        	   i++;
               Element e = it.next();  
               List<Element> employers=e.elements();
               Iterator<Element> em=employers.iterator();
           }
       } catch (DocumentException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }
		return i;  
	}	
	@SuppressWarnings("unchecked")
	public List<String> parseEmployers(String fileName){
		SAXReader saxReader = new SAXReader();
		List<String> list=new ArrayList<String>();
		DbModel d = null;
		String r="";
        try {  
           Document document = saxReader.read(new File(fileName));  
           Element root = document.getRootElement();  
           List<Element> elements = root.elements();  
           Iterator<Element> it = elements.iterator();  
           while(it.hasNext()) {  
               Element e = it.next();  
               System.out.println(e.getName()+"??");
               
               List<Element> employers=e.elements();
               Iterator<Element> em=employers.iterator();
               d=new DbModel();
               while(em.hasNext()){
            	    
            	   Element Subem=em.next();
            	   System.out.println(Subem.getName()+":"+Subem.getText());
            	   if(Subem.getName()=="cardType"){
            		   String cardtype=Subem.getText();
            		   d.setCardType(cardtype);
            	   }else if(Subem.getName()=="role"){
            		   String role=Subem.getText();
            		   d.setRolename(role);	   
            	   }else if(Subem.getName()=="roleNum"){
            		   String roleid=Subem.getText();
            		   d.setRoleid(Integer.parseInt(roleid));
            	   }else if(Subem.getName()=="name"){
            		   String uname=Subem.getText();
            		   d.setUsername(uname);
            	   }else if(Subem.getName()=="number"){
            		   String uid=Subem.getText();
            		   d.setUid(Integer.parseInt(uid));
            	   }
            	   
               }
               r=d.getCardType()+","+d.getUsername()+","+d.getUid()+","+d.getRolename()+","+d.getRoleid()+" ";
               list.add(r);
           }
       } catch (DocumentException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }
		return list;  
	}
	@SuppressWarnings("unchecked")
	public List<String> parseTagXml(String fileName){
		SAXReader saxReader = new SAXReader();
		List<String> list=new ArrayList<String>();
		DbModel d = null;
		String r="";
        try {  
           Document document = saxReader.read(new File(fileName));  
           Element root = document.getRootElement();  
           List<Element> elements = root.elements();  
           Iterator<Element> it = elements.iterator();  
           while(it.hasNext()) {  
               Element e = it.next();  
               System.out.println(e.getName()+"??");
               
               List<Element> employers=e.elements();
               Iterator<Element> em=employers.iterator();
               d=new DbModel();
               while(em.hasNext()){
            	    
            	   Element Subem=em.next();
            	   System.out.println(Subem.getName()+":"+Subem.getText());
            	   if(Subem.getName()=="cardType"){
            		   String cardtype=Subem.getText();
            		   d.setCardType(cardtype);
            	   }else if(Subem.getName()=="deviceType"){
            		   String deviceType=Subem.getText();
            		   d.setDeviceType(deviceType);	   
            	   }else if(Subem.getName()=="deviceNum"){
            		   String deviceNum=Subem.getText();
            		   d.setDeviceNum(deviceNum);
            	   }else if(Subem.getName()=="tagArea"){
            		   String tagArea=Subem.getText();
            		   d.setTagArea(tagArea);
            	   }else if(Subem.getName()=="tagAreaNum"){
            		   String tagAreaNum=Subem.getText();
            		   d.setTagAreaNum(tagAreaNum);
            	   }
            	   
               }
               r=d.getCardType()+","+d.getTagArea()+","+d.getDeviceType()+","+d.getDeviceNum()+","+d.getTagAreaNum()+" ";
               list.add(r);
           }
       } catch (DocumentException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }
		return list;  
	}
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		ParseXml p=new ParseXml();
		List<DbModel> list=p.parseRolesTable("E:/RolesTable.xml");
		Iterator it=list.iterator();
		while(it.hasNext()){
			DbModel d=(DbModel) it.next();
			System.out.println(d.getTableitem());
		}
	}
}
	
