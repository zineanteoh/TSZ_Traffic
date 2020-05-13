/*
Code taken from 
javabypatel.blogspot.com
"Running Threads In Sequence In Java"
*/

package tsz_traffic;

public class ResourceLock {
    // volatile keyword allows the variable to be stored in main memory
    // this allows the variable to be accessible by multiple threads
    public volatile int flag = 1;
    
}
