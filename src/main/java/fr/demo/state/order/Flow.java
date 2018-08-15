package fr.demo.state.order;

public enum Flow {
    /**
     * réapprovisionnement des stocks
     **/
    TO_STOCK,
    /**
     * préparation à partir des stocks
     **/
    FROM_STOCK,
    /**
     * JUST IN TIME => flux tendu
     **/
    JIT
}
