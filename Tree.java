/* GROUP
 * Ayushi Srivastava : 2016025
 * Tanish Gupta : 2016106
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;



class Node implements Serializable
{
	Node next;
    Node parent;
	ArrayList<Node> ptrs;
	ArrayList<String> keys;
	ArrayList<Record> rec;
	int isLeafNode;
	int num_keys;
	
	Node()
	{
		ptrs=new ArrayList<Node>();
		keys=new ArrayList<String>();
		rec=new ArrayList<Record>();
		isLeafNode=0;
		next=null;
		num_keys=0;
		parent=null;
	}
	void Leaf_Node()
	{
		
	}
	
}

class Record implements Serializable 
{
	String tag;
	String iid;
	String iname;
	String dep;
	String sal;
	
	Record(String a, String b, String c, String d, String e)
	{
		tag=a;
		iid=b;
		iname=c;
		dep=d;
		sal=e;
	}
	
	public String toString()
	{
		return(tag+","+iid+","+iname+","+dep+","+sal);
	}
	
	@Override
	public boolean equals(Object r)
	{
		Record r1=(Record)r;
		if(r1.iid.compareTo(iid)==0)
		{
			return true;
		}
		return false;
	}
}




public class Tree {

	//Node root;
	int order=10;
	TreeMap<String,ArrayList<Record>> hm=new TreeMap<String,ArrayList<Record>>();
	
	Node findLeafNode(Node root , String key)
	{
		int pos;
		Node t=root;
		if(t==null)
		{
			System.out.println("Empty tree");
			return t;
		}
		
		while(t.isLeafNode ==0)
		{
			pos=0;
			while(pos<t.num_keys)
			{
				if(key.compareTo(t.keys.get(pos))>=0) //check for Integer comparison and duplicates
				{
					pos++;
				}
				else 
					break;
			}
			t=t.ptrs.get(pos);					
		}
		return t;
	}
        
        Node insert_into_leaf_after_splitting(Node root, Node leaf_node, String key, Record r)
        {
            Node new_leaf_node=new Node();
            new_leaf_node.isLeafNode=1;
            
            Node temp;
            int pos=0;
            
            while(pos<order-1 && leaf_node.keys.get(pos).compareTo(key)<=0) //check for Integer comparison and duplicates    
                pos++;
           // System.out.println(pos+" "+key);
            
            temp=leaf_node;
            temp.keys.add(pos, key);
            temp.rec.add(pos, r);
            temp.num_keys++;
            
            //System.out.println(temp.rec.get(3).iname);
            leaf_node.num_keys=0;
            
            //dividing the original node into two
            for(int i=0;i<order/2;i++)
            {
                leaf_node.rec.set(i,temp.rec.get(i));
                leaf_node.keys.set(i,temp.keys.get(i));
                leaf_node.num_keys++;
            }
           
            for(int i=order/2;i<order;i++)
            {
            	//System.out.println(temp.rec.get(i).iid);
            	//System.out.println(i-order/2);
                new_leaf_node.rec.add(temp.rec.get(i));
                new_leaf_node.keys.add(temp.keys.get(i));
                new_leaf_node.num_keys++;
                
            }
            
            //new_leaf_node.rec.set(order-1, leaf_node.rec.get(order-1)); //check what this is doing
            new_leaf_node.next=leaf_node.next;
            leaf_node.next=new_leaf_node;  
             
            for(int i=leaf_node.num_keys;i<=order-1;i++)
            {
            	leaf_node.rec.remove(order/2);
            	leaf_node.keys.remove(order/2);
            }
           
            //for(int i=new_leaf_node.num_keys;i<order-1;i++)
            //    new_leaf_node.rec.set(i,null);
            
            
            
            new_leaf_node.parent=leaf_node.parent;
            String new_key=new_leaf_node.keys.get(0);
            
            
            return insert_into_parent(root,leaf_node,new_leaf_node,new_key);
        }
        
        Node insert_into_parent(Node root, Node left, Node right, String key)
        {
            Node left_par= left.parent;
            //if left_par is root
            if(left_par==null)
            {
            	
                Node rt=new Node();
                rt.keys.add(key);
                rt.ptrs.add(left);
                rt.ptrs.add(right);
                rt.num_keys++;
                left.parent=rt;
                right.parent=rt;
                return rt;
            }
            
          //Find the parent's pointer to the left  node
            
            int left_pos=0;
            while(left_pos <= left_par.num_keys && left_par.ptrs.get(left_pos) != left)
                left_pos++;
            
            //if space available 
            if(left_par.num_keys<order-1)
            {
                left_par.ptrs.add(left_pos+1, right);
                left_par.keys.add(left_pos, key);
                left_par.num_keys++;
                return root;
            }
            
            //if space not-available
            return insert_into_node_after_splitting(root,left_par, right,key, left_pos);
            
        }
        
        Node insert_into_node_after_splitting(Node root, Node old_node, Node right, String key, int left_pos)
        {
            Node temp=old_node;
            temp.ptrs.add(left_pos+1, right);
            temp.keys.add(left_pos, key);
            temp.num_keys++;
            //System.out.println(old_node.keys);

            Node new_node=new Node();
            old_node.num_keys=0;
            
            int i=0;
            for( i=0;i<order/2;i++)  //(order+1)/2-1
            {
                old_node.ptrs.set(i,temp.ptrs.get(i));
                old_node.keys.set(i,temp.keys.get(i));
                old_node.num_keys++;
            }
            old_node.ptrs.set(i,temp.ptrs.get(i));
            //System.out.println(old_node.num_keys);
            //System.out.println(old_node.keys);
            String k_prime=temp.keys.get(order/2); //(order+1)/2-1
            int j=0;
            for(++i,j=0;i<order;i++,j++)
            {
                new_node.ptrs.add(temp.ptrs.get(i));
                new_node.keys.add(temp.keys.get(i));
                new_node.num_keys++;
            }
            new_node.ptrs.add(temp.ptrs.get(i));
            new_node.parent=old_node.parent;
            Node child;
            for(i=0;i<= new_node.num_keys;i++)
            {
                child=new_node.ptrs.get(i);
                child.parent=new_node;
            }
            //System.out.println(old_node.num_keys);
            
            //removing extra nodes
            for(int k=old_node.num_keys;k<=order-1;k++)
            {
            	old_node.ptrs.remove(order/2+1); //(order+1)/2
            	old_node.keys.remove(order/2); //(order+1)/2-1
            }
           // System.out.println(old_node.keys);
            
            return insert_into_parent(root, old_node, new_node, k_prime);
            
        }
	Node insert(Node rt, String key, Record r)
	{
		if(rt==null)
		{
                    rt=new Node();
                    rt.isLeafNode=1;
                    rt.keys.add(key);
                    rt.rec.add(r);
		   // rt.ptrs.add(order-1, null);
		    rt.num_keys++;
		    rt.parent=null;
                    
                    return rt;
		}
		
		//find the leaf node containing the key to be inserted
		Node leaf_node=findLeafNode(rt, key);
		
		//Case 1: When the leaf node has space.
		if(leaf_node.num_keys<order-1)
		{
			//System.out.println("leaf node has space");
			int insert_at_pos=0;
			while(insert_at_pos<leaf_node.num_keys && leaf_node.keys.get(insert_at_pos).compareTo(key)<=0) //check for equality
			{
				insert_at_pos++;
			}
			
			leaf_node.keys.add(insert_at_pos, key);
			leaf_node.rec.add(insert_at_pos, r);
			leaf_node.num_keys++;
			return rt;
		}
		
		//Case 2: when the leaf is full and leaf must split
		//System.out.println(r.iname);
		return insert_into_leaf_after_splitting(rt, leaf_node,key,r);
		
	}
	
	
	//Deletion
	Node delete(Node root, String key, Record r)
	{
		//Finding leaf
		Node leaf_node=findLeafNode(root,key); // This returns the appropriate leaf node
		if(leaf_node==null)
			return root;
		//System.out.println(leaf_node.num_keys);
		int flag=0;
		for(int i=0;i<leaf_node.num_keys;i++)
		{
			//System.out.println(leaf_node.keys.get(i));
			if(leaf_node.keys.get(i).compareTo(key)==0)
				flag=1;
		}
		
		if(flag==0)
		{
			System.out.println("Key not found");
			return root;
		}
		
		
		//Record Found
		root=delete_record(root,leaf_node,key,r);
		
		return root;
		
		
	}
	//delete record from the leaf node 
	Node delete_record(Node root,Node n, String key, Record r)
	{
		//Remove key and record from the leaf_node
		n.keys.remove(key);
		n.rec.remove(r);
		n.num_keys--;
		//System.out.println(n.keys);
		//verify for resulting in empty tree after deletion
		
		int min_keys=order/2;
		//if number of keys is greater than min req
		if(n.num_keys>=min_keys)
			return root;
		
		//node falls below min - either coalesce or redistribute 
		int neighbour_index=n.parent.ptrs.indexOf(n)-1;
		int k_prime_index;
		String k_prime;
		Node neighbour;
		if(neighbour_index==-1)
		{
			k_prime_index=0;
			neighbour=n.parent.ptrs.get(1);
		}
		else
		{
			k_prime_index=neighbour_index;
			neighbour=n.parent.ptrs.get(neighbour_index);
		}
		
		k_prime=n.parent.keys.get(k_prime_index);
		
		//merge nodes or redistribute
		if(neighbour.num_keys+n.num_keys<order)
			return merge_nodes(root, n, neighbour, neighbour_index, k_prime);
		else 
			return redistribute_nodes(root, n, neighbour, neighbour_index,k_prime_index, k_prime);
		
	}
	
	Node merge_nodes(Node root, Node n, Node neighbour, int neighbour_index, String k_prime)
	{
		//System.out.println("merge"+k_prime);
		Node temp;
		// Swapping the right node with the leftmost inorder to have the same functionality of merging the nodes with left.
		if(neighbour_index==-1)
		{
			
			temp=n;
			n=neighbour;
			neighbour=temp;
		}
		int neighbour_inserion_index=neighbour.num_keys;
		
		if(n.isLeafNode==0)
		{
			neighbour.keys.add(k_prime);
			neighbour.num_keys++;
		
			int end=n.num_keys;
			int j;
			for(j=0;j<end;j++)
			{
				neighbour.keys.add(n.keys.get(j));
				neighbour.ptrs.add(n.ptrs.get(j));
				neighbour.num_keys++;
				n.num_keys--;
				
			}
			
			neighbour.ptrs.add(n.ptrs.get(j));
			// All children must point to the same parent.
			
			for(int i=0;i<neighbour.num_keys+1;i++)
			{
				temp=neighbour.ptrs.get(i);
				temp.parent=neighbour;
			}
			
		}
		else
		{
			//System.out.println("hi");
			for(int j=0;j<n.num_keys;j++)
			{
				neighbour.keys.add(n.keys.get(j));
				neighbour.rec.add(n.rec.get(j));
				neighbour.num_keys++;
			}
			neighbour.next=n.next; //not sure - check it
		}
		
		root=delete_internal_node(root,n.parent,k_prime, n);
		return root;
	}
	
	Node redistribute_nodes(Node root, Node n, Node neighbour, int neighbour_index,int k_prime_index, String k_prime)
	{
		
		//n has neighbour to the left 
		if(neighbour_index != -1)
		{
			
			if(n.isLeafNode==0)
			{
				n.ptrs.add(n.ptrs.get(n.num_keys));
				
				n.keys.add(n.keys.get(n.num_keys-1));
				n.ptrs.set(n.num_keys,n.ptrs.get(n.num_keys-1));
				for(int j=n.num_keys-1;j>0;j--)
				{
					n.keys.set(j, n.keys.get(j-1));
					n.ptrs.set(j, n.ptrs.get(j-1));
				}   
				
				n.ptrs.set(0, neighbour.ptrs.get(neighbour.num_keys));
				Node temp=n.ptrs.get(0);
				temp.parent=n;
				neighbour.ptrs.remove(neighbour.num_keys);
				n.keys.set(0, k_prime);
				n.parent.keys.set(k_prime_index, neighbour.keys.get(neighbour.num_keys-1)); //check once again
				neighbour.keys.remove(neighbour.num_keys-1);
				
			}
			else
			{
				
				n.keys.add(n.keys.get(n.num_keys-1));
				n.rec.add(n.rec.get(n.num_keys-1));
				for(int j=n.num_keys-1;j>0;j--)
				{
					n.keys.set(j, n.keys.get(j-1));
					n.rec.set(j, n.rec.get(j-1));
				}
				
				n.rec.set(0, neighbour.rec.get(neighbour.num_keys-1));
				neighbour.rec.remove(neighbour.num_keys-1);
				n.keys.set(0, neighbour.keys.get(neighbour.num_keys-1));
				n.parent.keys.set(k_prime_index, n.keys.get(0)); // check
				//System.out.println(neighbour.keys);
				neighbour.keys.remove(neighbour.num_keys-1);
				
		
			}
			
			
		}
		
		else
		{
			
			if(n.isLeafNode==1)
			{
				n.keys.add(neighbour.keys.get(0));
				
				n.rec.add(neighbour.rec.get(0));
				n.parent.keys.set(k_prime_index, neighbour.keys.get(1)); //check
				
				for(int i=0;i<neighbour.num_keys-1;i++)
				{
					neighbour.keys.set(i, neighbour.keys.get(i+1));
					neighbour.rec.set(i, neighbour.rec.get(i+1));
				}
				neighbour.keys.remove(neighbour.num_keys-1);
				neighbour.rec.remove(neighbour.num_keys-1);
			}
			else
			{
				n.keys.add(k_prime);
				n.ptrs.add(neighbour.ptrs.get(0));
				Node temp=n.ptrs.get(n.num_keys+1);
				temp.parent=n;
				n.parent.keys.set(k_prime_index, neighbour.keys.get(0)); // check
				int i;
				for(i=0;i<neighbour.num_keys-1;i++)
				{
					neighbour.keys.set(i, neighbour.keys.get(i+1));
					neighbour.ptrs.set(i, neighbour.ptrs.get(i+1));
				}
				neighbour.ptrs.set(i, neighbour.ptrs.get(i+1));
				
				neighbour.keys.remove(neighbour.num_keys-1);
				neighbour.ptrs.remove(neighbour.num_keys);
			}
			
		}
		
		n.num_keys++;
		neighbour.num_keys--;
		return root;
	}
	
	Node delete_internal_node(Node root, Node n, String key, Node ptr)
	{
		//Remove key and record from the leaf_node
		n.keys.remove(key);
		n.ptrs.remove(ptr);
		n.num_keys--;
		
		//deletion from the root
		if(n==root)
		{
			Node new_root;
			//non-empty root
			if(root.num_keys>0)
				return root;
			//empty root
			new_root=root.ptrs.get(0);
			new_root.parent=null;
			
			return new_root;
		}
		
		int min_keys=(order+1)/2-1;
		if(n.num_keys>=min_keys)
			return root;
		
		int neighbour_index=n.parent.ptrs.indexOf(n)-1;
		int k_prime_index;
		String k_prime;
		Node neighbour;
		if(neighbour_index==-1)
		{
			k_prime_index=0;
			neighbour=n.parent.ptrs.get(1);
		}
		else
		{
			k_prime_index=neighbour_index;
			neighbour=n.parent.ptrs.get(neighbour_index);
		}
		
		k_prime=n.parent.keys.get(k_prime_index);
		
		if(neighbour.num_keys+n.num_keys<order-1)
			return merge_nodes(root, n, neighbour, neighbour_index, k_prime);
		else 
			return redistribute_nodes(root, n, neighbour, neighbour_index,k_prime_index, k_prime);
		
		
	}
	
	
	void display_tree(Node root)
	{
		ArrayList<Node> queue=new ArrayList<Node>();
		System.out.println(root.keys);
		if(root.isLeafNode==0)
			queue.addAll(root.ptrs);
		while(true)
		{
			int node_count=queue.size();
			if(node_count==0)
				break;
			while(node_count>0)
			{
				Node temp=queue.remove(0);
				System.out.print(temp.keys);
				if(temp.isLeafNode==0)
					queue.addAll(temp.ptrs);
				
				node_count--;
			}
			System.out.println();
		}
	}
	
	
	
	public static void main(String[] args) throws IOException
	{
		Node rt =null;
		Tree t=new Tree();
		/*
		if(rt==null)
			System.out.println("null");
		else
			System.out.println("notnull");
		*/
		/*
		Record[] rec=new Record[20];
		rec[0]=new Record("00","04","a","cs","30");
		rec[1]=new Record("00","09","b","cs","30");
		rec[2]=new Record("00","16","c","cs","30");
		rec[3]=new Record("00","25","d","cs","30");
		rec[4]=new Record("00","01","e","cs","30");
		rec[5]=new Record("00","20","f","cs","30");
		rec[6]=new Record("00","13","g","cs","30");
		rec[7]=new Record("00","15","g","cs","30");
		rec[8]=new Record("00","10","f","cs","30");
		rec[9]=new Record("00","11","g","cs","30");
		rec[10]=new Record("00","12","g","cs","30");
		rec[11]=new Record("00","26","g","cs","30");
		rec[12]=new Record("00","28","g","cs","30");
		rec[13]=new Record("00","29","g","cs","30");
		rec[14]=new Record("00","30","g","cs","30");
		rec[15]=new Record("00","32","g","cs","30");
		
		rt=t.insert(rt,"04",rec[0]);
		rt=t.insert(rt,"09",rec[1]);
		rt=t.insert(rt,"16",rec[2]);
		rt=t.insert(rt,"25",rec[3]);
		rt=t.insert(rt,"01",rec[4]);
		rt=t.insert(rt,"20",rec[5]);
		rt=t.insert(rt,"13",rec[6]);
		rt=t.insert(rt,"15",rec[7]);
		rt=t.insert(rt,"10",rec[8]);
		rt=t.insert(rt,"11",rec[9]);
		rt=t.insert(rt,"12",rec[10]);
		rt=t.insert(rt,"26",rec[11]);
		rt=t.insert(rt,"28",rec[12]);
		rt=t.insert(rt,"29",rec[13]);
		rt=t.insert(rt,"30",rec[14]);
		rt=t.insert(rt,"32",rec[15]);
	//	System.out.println(rt.num_keys+" "+rt.keys+rt.ptrs.get(2).keys+" "+rt.rec.toString());
		rt=t.delete(rt,"16", rec[6]);
	//	rt=t.delete(rt,"25", rec[7]);
	//	rt=t.delete(rt,"13", rec[8]);
	//	rt=t.delete(rt,"11", rec[2]);
	//	System.out.println(rt1.keys);
		
		t.display_tree(rt);
		*/
		
		int fn=3;
		try 
		{
			File fi=new File("record_datafile.txt");
			FileReader f=new FileReader(fi);
			BufferedReader br = new BufferedReader(f);
			String line = br.readLine();
			while (line != null) {
				//String[] s1 = line.split(",");
				//System.out.println(s1[0]);
				String[] s = line.split(",");
				Record rec = new Record(s[0],s[1],s[2],s[3],s[4]);
				
				if(s[0].compareTo("0000")!=0 && t.hm.containsKey(s[fn]))
				{
					t.hm.get(s[fn]).add(rec);
				}
				else if(s[0].compareTo("0000")!=0 && !t.hm.containsKey(s[fn]))
				{
					ArrayList<Record> arr=new ArrayList<Record>();
					arr.add(rec);
					t.hm.put(s[fn], arr);
					rt=t.insert(rt, s[fn], rec);
				}
				line = br.readLine();
				
			}
		f.close();
		}
		finally
		{
			
		
		}
		
		t.display_tree(rt);
		
		while(true)
		{
			
			BufferedReader bfr= new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter option ");
			int option=Integer.parseInt(bfr.readLine());
			if(option==1)
			{
				System.out.print("Enter the Value to be found ");
				String s1=bfr.readLine();
				Node leaf_node=t.findLeafNode(rt, s1);
				int pos=0;
				while(pos<leaf_node.num_keys)
				{
					if(s1.compareTo(leaf_node.keys.get(pos))!=0) //check for Integer comparison and duplicates
					{
						pos++;
					}
					else 
						break;
				}
				if(t.hm.containsKey(s1))
					System.out.println("The position of this value in the leaf node is :" +pos+"The first record is : "+t.hm.get(s1).get(0).toString());
				else
					System.out.println("Key doesnt exists");
			}
			else if(option==2)
			{
				System.out.print("Enter the Value to be found ");
				String s1=bfr.readLine();
				Node leaf_node=t.findLeafNode(rt, s1);
				int pos=0;
				while(pos<leaf_node.num_keys)
				{
					if(s1.compareTo(leaf_node.keys.get(pos))!=0) 
					{
						pos++;
					}
					else 
						break;
				}
				if(t.hm.containsKey(s1))
				{	
					for(int i=0;i<t.hm.get(s1).size();i++)
					{
						System.out.println(t.hm.get(s1).get(i).toString());
					}
				}
				else
				{
					System.out.println("Key doent exists");
				}
					
			}
			else if(option==3)
			{
				System.out.print("Enter intial value");
				String st=bfr.readLine();
				System.out.print("Enter final value");
				String end=bfr.readLine();
				Set<String> keys=t.hm.keySet();
				for(String key: keys)
				{
					if((key.compareTo(st)>=0) && (key.compareTo(end)<=0))
					{
							System.out.println("This is the value "+key);
							System.out.println("The records are as follows :");
							for(Record re: t.hm.get(key))
							{
								System.out.println(re.toString());
							}
					}
				}
				
			}
			else if(option ==4)
			{
				System.out.print("Enter record to be inserted ");
				String s=bfr.readLine();
				String s1[]=s.split(",");
				Record rec = new Record(s1[0],s1[1],s1[2],s1[3],s1[4]);
				
				if(t.hm.containsKey(s1[fn]))
				{
					t.hm.get(s1[fn]).add(rec);
				}
				else
				{
					ArrayList<Record> arr=new ArrayList<Record>();
					arr.add(rec);
					t.hm.put(s1[fn], arr);
					rt=t.insert(rt, s1[fn], rec);
				}
			
				t.display_tree(rt);
				
				//append in file
				try 
				{
					File fi=new File("record_datafile.txt");
					FileWriter f=new FileWriter(fi,true);
					BufferedWriter br = new BufferedWriter(f);
					PrintWriter pw = new PrintWriter(br);
					//f.write("\n");
					
					pw.print(s);
					pw.println();
					pw.close();
					f.close();
				}
				finally
				{
				}
				
			}
			else if(option == 5)
			{
			
				System.out.print("Enter record to be deleted ");
				String s1[]=bfr.readLine().split(",");
				Record rec = new Record(s1[0],s1[1],s1[2],s1[3],s1[4]);
				int size=0;
				/*
				if(t.hm.containsKey(s1[fn]))
				{
					for(int i=0;i<t.hm.get(s1[fn]).size();i++)
					{
						if(t.hm.get(s1[fn]).get(i).tag.compareTo("0000")!=0)
							size++;
					}
				}
				*/
				System.out.println(t.hm.containsKey(s1[fn])+" "+t.hm.get(s1[fn]).contains(rec));
				if(t.hm.containsKey(s1[fn]) && t.hm.get(s1[fn]).contains(rec) && t.hm.get(s1[fn]).size()>1)
				{
					t.hm.get(s1[fn]).remove(rec);
					rec.tag="0000";
					//t.hm.get(s1[fn]).add(rec);
					System.out.println("deleted from buffer");

					//update in file
					try 
					{
						File fi=new File("record_datafile.txt");
						File fj=new File("record_datafile1.txt");
						//System.out.println(fi.exists());
						FileReader f=new FileReader(fi);
						BufferedReader br = new BufferedReader(f);
						FileWriter fw=new FileWriter(fj);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter pw = new PrintWriter(bw);
						int lineno=0;
						String line = br.readLine();
						//System.out.println(line+"line");
						while (line != null) {
							//System.out.println("IN LOOPPPP");
							
							//String[] s1 = line.split(",");
							//System.out.println(s1[0]);
							String[] s = line.split(",");
							if(s[1].compareTo(s1[1])==0)
							{
							     pw.print(rec.toString());
							     pw.println();
								//break;
							}
							else {
							pw.print(line);
							pw.println();
							}
							line = br.readLine();
						}
					f.close();
					pw.close();
					fi.delete();
					fj.renameTo(fi);
					}
					finally
					{
						
					}
					
				}
				else if (t.hm.containsKey(s1[fn]) && t.hm.get(s1[fn]).contains(rec) && t.hm.get(s1[fn]).size()==1)
				{
					System.out.println("fle");
					t.hm.get(s1[fn]).remove(rec);
					t.hm.remove(s1[fn]);
					rec.tag="0000";
					//t.hm.get(s1[fn]).add(rec);
					
					//update in file
					try 
					{
						File fi=new File("record_datafile.txt");
						File fj=new File("record_datafile1.txt");
						//System.out.println(fi.exists());
						FileReader f=new FileReader(fi);
						BufferedReader br = new BufferedReader(f);
						FileWriter fw=new FileWriter(fj);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter pw = new PrintWriter(bw);
						int lineno=0;
						String line = br.readLine();
						//System.out.println(line+"line");
						while (line != null) {
							//System.out.println("IN LOOPPPP");
							
							//String[] s1 = line.split(",");
							//System.out.println(s1[0]);
							String[] s = line.split(",");
							if(s[1].compareTo(s1[1])==0)
							{
							     pw.print(rec.toString());
							     pw.println();
								//break;
							}
							else {
							pw.print(line);
							pw.println();
							}
							line = br.readLine();
						}
					f.close();
					pw.close();
					fi.delete();
					fj.renameTo(fi);
					}
					finally
					{
						
					}
					
					rt=t.delete(rt, s1[fn], rec);
					t.display_tree(rt);
					
				}
				else
				{
					System.out.println("Record not found " );
				}
				
				
			}
			else if (option ==6)
				break;
			
		}
		
		ObjectOutputStream UsersList=null;	
		
		try	
		{	
			UsersList=new ObjectOutputStream(new FileOutputStream("index.txt"));	
			UsersList.writeObject(rt);			
		}	
		finally	
		{	
			UsersList.close();	
		}	
		
		
	}

}
