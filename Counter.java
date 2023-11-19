import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Counter{
  private Node head;
  public Counter() { head=new Node(0); }
  public void inc() {
    head.lock();
    head.count=head.count+1;
    head.unlock();
    }
  public void display(){ System.out.println("\t"+head.count+"\n");}
 /* Node*/
  private class Node {
    int count;
    Lock lock;
    Node(int item) {
     this.count = item;
     //this.lock=new ReentrantLock();
     //this.lock=new LockOne();
     //this.lock=new LockTwo();
     //this.lock=new Peterson();
     //this.lock=new TASLock();
     //this.lock=new TTASLock();
   //this.lock = new BackoffLock(10, 54);
    //this.lock=new ALock(14);
    //this.lock=new CLHLock();
    this.lock=new MCSLock();
     //this.lock=new HBOLock();
    }
    void lock() {lock.lock();}
    void unlock() {lock.unlock();}
  }
}
