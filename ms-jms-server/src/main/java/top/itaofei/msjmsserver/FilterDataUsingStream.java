package top.itaofei.msjmsserver;

import java.util.*;
import javax.xml.crypto.Data;

public class FilterDataUsingStream {

  public static void main(String[] args) {
    // Sample data
    List<DataObj> dataList = new ArrayList<>();
    dataList.add(new DataObj(1, 100));
    dataList.add(new DataObj(2, 200));
    dataList.add(new DataObj(1, 300));
    dataList.add(new DataObj(1, 200));
    dataList.add(new DataObj(3, 400));
    dataList.add(new DataObj(2, 500));

    // Filter data using Stream API
    List<DataObj> filteredDataList = dataList.stream()
        .collect(
            HashMap<Object, DataObj>::new,
            (map, data) -> {

              map.computeIfPresent(data.getSequenceNumber(), ( k, v) -> {
                if (data.getProcessTime() >= v.getProcessTime()) {
                  return data;
                }
                return v;
              });
              map.computeIfAbsent(data.getSequenceNumber(), k -> data);
            },
            Map::putAll
        )
        .values()
        .stream()
        .toList();

    // Print filtered data
    for (DataObj data : filteredDataList) {
      System.out.println(data.getSequenceNumber() + " " + data.getProcessTime());
    }
  }
}

class DataObj {

  private int sequenceNumber;
  private int processTime;

  public DataObj(int sequenceNumber, int processTime) {
    this.sequenceNumber = sequenceNumber;
    this.processTime = processTime;
  }

  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public int getProcessTime() {
    return processTime;
  }
}
