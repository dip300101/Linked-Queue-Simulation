/**
 * I, Deep Patel, 000818379 certify that this material is my original work.
 * No other person's work has been used without due acknowledgement.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main {

  public static void main(String[] args) {

    LinkedQueue<Customer>[] express, normal;


    int f, n, x, c;

    try {

      Scanner s = new Scanner(new File("src/CustomerData.txt"));


      if (s.hasNextLine()) {
        f = s.nextInt();
        n = s.nextInt();
        x = s.nextInt();
        c = s.nextInt();


        express = new LinkedQueue[f];
        for (int i = 0; i < express.length; i++)
          express[i] = new LinkedQueue<>();
        normal = new LinkedQueue[n];
        for (int i = 0; i < normal.length; i++)
          normal[i] = new LinkedQueue<>();


        scanCustomerItems(express, normal, c, x, s);


        sortQueues(express);
        sortQueues(normal);
        System.out.println("\nPART A: OPTIMAL INITIAL STATE OF QUEUES");

        int longQueTime = Math.max(serviceTime(express[0]), serviceTime(normal[0]));

        int serviceTime = printQueues(express, LaneType.EXPRESS) + printQueues(normal, LaneType.NORMAL);
        System.out.println("Cumulative service time of all Customers in store = " + serviceTime + " s");
        System.out.println("Total time to clear all Customers out of store = " + longQueTime + " s\n\n");


        System.out.println("PART B: SIMULATION RESULTS OF DEQUEUE");

        runSimulation(express, normal, longQueTime, 30);
      }
      s.close();
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }


  private static void scanCustomerItems(
      LinkedQueue<Customer>[] express, LinkedQueue<Customer>[] normal, int c, int x, Scanner s) {

    for (int i = 0; i < c; i++) {
      int[] fServiceTimes, nServiceTimes;

      int fMinIndex = 0;
      fServiceTimes = serviceTime(express);
      for (int j = 1; j < fServiceTimes.length; j++)
        fMinIndex = fServiceTimes[j] <= fServiceTimes[fMinIndex] ? j : fMinIndex;


      int nMinIndex = 0;
      nServiceTimes = serviceTime(normal);
      for (int j = 1; j < nServiceTimes.length; j++)
        nMinIndex = nServiceTimes[j] <= nServiceTimes[nMinIndex] ? j : nMinIndex;

      Customer customer = new Customer(s.nextInt());


      if ( customer.getItems() <= x && fServiceTimes[fMinIndex] <= nServiceTimes[nMinIndex])
        express[fMinIndex].enqueue(customer);

      else
        normal[nMinIndex].enqueue(customer);
    }
  }


  public static int[] serviceTime(LinkedQueue<Customer>[] qs) {
    int[] evaluation = new int[qs.length];
    for (int j = 0; j < qs.length; j++)
      evaluation[j] = serviceTime(qs[j]);
    return evaluation;
  }



  public static int serviceTime(LinkedQueue<Customer> q) {
    int t = 0;
    if(!q.isEmpty()) {
      Customer frontValue = q.dequeue();
      q.enqueue(frontValue);
      t += frontValue.serviceTime();


      while (q.peek() != frontValue) {
        Customer temp = q.dequeue();
        t += temp.serviceTime();
        q.enqueue(temp);
      }
    }
    return t;
  }


  public static void sortQueues(LinkedQueue<Customer>[] qs) {
    int startScan;
    int index;
    int minIndex;
    LinkedQueue<Customer> minValue;


    for (startScan = 0; startScan < (qs.length - 1); startScan++)
    {

      minIndex = startScan;
      minValue = qs[startScan];


      for (index = startScan + 1; index < qs.length; index++)
      {
        if (serviceTime(qs[index]) > serviceTime(minValue))
        {
          minValue = qs[index];
          minIndex = index;
        }
      }

      qs[minIndex] = qs[startScan];
      qs[startScan] = minValue;
    }
  }


  public static int printQueues(LinkedQueue<Customer>[] qs, LaneType type) {
    StringBuilder checkoutLanesString = new StringBuilder();
    int qTime, totalTime = 0;

    for (int i = 0; i < qs.length; i++) {
      qTime = serviceTime(qs[i]);
      totalTime += qTime;
      checkoutLanesString.append(String.format(
          "Checkout(%-7s) #%d (ServiceTime = %d s) = %s\n",
          type,
          i+1,
          qTime,
          qs[i].toString()));
    }
    System.out.println(checkoutLanesString.toString());
    return totalTime;
  }


  public static void runSimulation(
      LinkedQueue<Customer>[] express, LinkedQueue<Customer>[] normal, int longQueTime, int simulationStep) {

    printHeadings(express, normal);


    int timeElapsed = 0;
    int[] fServiceTimes = new int[express.length];
    int[] nServiceTimes = new int[normal.length];


    while (timeElapsed <= longQueTime + (simulationStep - (longQueTime % simulationStep))) {

      if (timeElapsed % simulationStep == 0) printIntervalResult(express,normal, timeElapsed);

      dequeueCustomers(express, normal, fServiceTimes, nServiceTimes);

      for (int i = 0; i < fServiceTimes.length; i++) fServiceTimes[i]++;
      for (int i = 0; i < nServiceTimes.length; i++) nServiceTimes[i]++;

      timeElapsed++;
    }
  }


  private static void dequeueCustomers(
      LinkedQueue<Customer>[] express, LinkedQueue<Customer>[] normal, int[] fServiceTimes, int[] nServiceTimes) {

    for (int i = 0; i < express.length; i++) {
      if (!express[i].isEmpty()) {
        if (fServiceTimes[i] == express[i].peek().serviceTime()) {
          fServiceTimes[i] = 0;
          express[i].dequeue();
        }
      }
    }


    for (int i = 0; i < normal.length; i++) {
      if (!normal[i].isEmpty()) {
        if (nServiceTimes[i] == normal[i].peek().serviceTime()) {
          nServiceTimes[i] = 0;
          normal[i].dequeue();
        }
      }
    }
  }


  private static void printHeadings(LinkedQueue<Customer>[] express, LinkedQueue<Customer>[] normal) {

    StringBuilder headings = new StringBuilder();
    headings.append(String.format("%-8s|", "t(s)"));
    int lineCount = 1;


    for (int i = 0; i < express.length; i++) {
      headings.append(String.format("| %-8s#%-3d|", LaneType.EXPRESS.toString(), lineCount));
      lineCount++;
    }


    lineCount = 1;
    for (int i = 0; i < normal.length; i++) {
      headings.append(String.format("| %-8s#%-3d|", LaneType.NORMAL.toString(), lineCount));
      lineCount++;
    }
    headings.append("|\n");
    headings.append("=========");
    for (int i = 0; i < express.length + normal.length; i++) {
      headings.append("===============");
    }
    headings.append("=");
    System.out.println(headings.toString());
  }


  private static void printIntervalResult(
      LinkedQueue<Customer>[] express, LinkedQueue<Customer>[] normal, int timeElapsed) {
    StringBuilder simulationResult = new StringBuilder();
    simulationResult.append(String.format("%-8d|", timeElapsed));


    for (int i = 0; i < express.length; i++) simulationResult.append(String.format("|      %-6d |", express[i].size()));

    for (int i = 0; i < normal.length; i++) simulationResult.append(String.format("|      %-6d |", normal[i].size()));
    simulationResult.append("|");

    simulationResult.append("\n---------");
    for (int i = 0; i < express.length + normal.length; i++) {
      simulationResult.append("---------------");
    }
    simulationResult.append("-");
    System.out.println(simulationResult.toString());  
  }
}