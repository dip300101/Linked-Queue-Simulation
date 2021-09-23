/**
 * This class represents a Customer with a property for the number of items - which is used to calculate the service
 * time for an instance of a Customer object.
 */
public class Customer {
  /** Number of items this Customer has in their cart **/
  private int items;

  /** Constructor initializes this Customer cart to be empty **/
  public Customer(int items) { this.items = items; }

  /**
   * Getter used to retrieve the current number of items (property) for this customer.
   * @return number of items this Customer has
   */
  public int getItems() { return items; }

  /**
   * Setter used to assign a number of items as this Customer's item property.
   * @param num number of items this customer has
   */
  public void setItems(int num) { items = num; }

  /**
   * Method used to calculate this customer's service time based on the number of items (property).
   * @return time in seconds that it takes to serve this Customer
   */
  public int serviceTime() { return 45 + 5  * items; }

  /**
   * Overridden method from the Object class to display this customers number of items and calculated service time.
   * @return formatted String as a vector representing items and service time in seconds
   */
  @Override
  public String toString() {
    return String.format("[items:%3d, service:%4ds]", items, this.serviceTime());
  }
}
