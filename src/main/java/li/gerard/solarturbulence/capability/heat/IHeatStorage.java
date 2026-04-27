package li.gerard.solarturbulence.capability.heat;


public interface IHeatStorage {

    /**
     * Adds heat to the storage. Returns the amount actually accepted.
     *
     * @param maxReceive amount of heat offered, in heat units
     * @param simulate   if true, do not actually modify the storage
     * @return amount of heat accepted
     */
    int receiveHeat(int maxReceive, boolean simulate);

    /**
     * Removes heat from the storage. Returns the amount actually extracted.
     *
     * @param maxExtract maximum heat requested, in heat units
     * @param simulate   if true, do not actually modify the storage
     * @return amount of heat extracted
     */
    int extractHeat(int maxExtract, boolean simulate);

    /** Current stored heat. */
    int getHeatStored();

    /** Maximum heat this storage can hold. */
    int getHeatCapacity();

    /** True if this storage can have heat added to it. */
    boolean canReceiveHeat();

    /** True if heat can be extracted from this storage. */
    boolean canExtractHeat();
}