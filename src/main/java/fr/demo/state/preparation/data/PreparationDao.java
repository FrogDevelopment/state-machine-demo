package fr.demo.state.preparation.data;

import java.util.List;

public interface PreparationDao {

    List<String> getOrderCodesOfPreparation(String code);
}
