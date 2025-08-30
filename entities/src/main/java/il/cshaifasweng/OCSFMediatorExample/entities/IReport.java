package il.cshaifasweng.OCSFMediatorExample.entities;

interface IReport {

    public String GetReportAsString();

    public int getTotalCustomers();

    public void setTotalCustomers(int totalCustomers);

    public int getDeliveryOrders();

    public void setDeliveryOrders(int deliveryOrders);

    public int getReservations();

    public void setReservations(int reservations);

    public int getComplaintsCount();

    public void setComplaintsCount(int complaintsCount);





}
