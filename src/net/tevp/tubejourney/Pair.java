package net.tevp.tubejourney;

import java.lang.*; 
import java.util.*; 

public class Pair<TYPEA, TYPEB> implements Comparable< Pair<TYPEA, TYPEB> > {
  protected final TYPEA first_;
  protected final TYPEB second_;

  public Pair(TYPEA first, TYPEB second) {
    first_   = first;
    second_ = second;
  }
  public TYPEA first() {
    return first_;
  }
  public TYPEB second() {
    return second_;
  }
  @Override
  public String toString() {
    System.out.println("in toString()");
    StringBuffer buff = new StringBuffer();
      buff.append("first: ");
      buff.append(first_);
      buff.append("\tsecond: ");
      buff.append(second_);
    return(buff.toString() );
  }
  @Override
  public int compareTo( Pair<TYPEA, TYPEB> p1 ) { 
    System.out.println("in compareTo()");
    if ( null != p1 ) { 
      if ( p1.equals(this) ) { 
        return 0; 
      } else if ( p1.hashCode() > this.hashCode() ) { 
            return 1;
      } else if ( p1.hashCode() < this.hashCode() ) { 
        return -1;  
      }
    }
    return(-1);
  }
  @Override
public boolean equals(Object o) { 
  System.out.println("in equals()");
  if (o instanceof Pair) { 
    Pair<?, ?> p1 = (Pair<?, ?>) o;
    if ( p1.first_.equals( this.first_ ) && p1.second_.equals( this.second_ ) ) { 
      return(true);
    }
  }
  return(false);
}
  
@Override
  public int hashCode() { 
    int hashCode = first_.hashCode() + (31 * second_.hashCode());
    System.out.println("in hashCode() [" + Integer.toString(hashCode) + "]");
    return(hashCode);
  }
}
