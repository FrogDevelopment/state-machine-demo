package fr.demo.state.pack.data;

public interface PackDao {

    String create(String orderCode);

    String getOrderCode(String packCode);
}
