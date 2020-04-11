package indi.eos.entities;

public class RangeEntity {
  private long start;
  private long end;

  public void setStart(long start) {
    this.start = start;
  }

  public long getStart() {
    return this.start;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public long getEnd() {
    return this.end;
  }

  public String getParameterValue() {
    return String.format("%d-%d", this.start, this.end);
  }
}
