export type SimCard = {
  /**
   * Unique identifier for the SIM card (subscription ID).
   */
  id: number;

  /**
   * The name assigned to the SIM card by the system, typically
   * corresponding to the carrier name or user-assigned label.
   */
  displayName: string;

  /**
   * The name of the mobile network carrier associated with the SIM.
   */
  carrierName: string;

  /**
   * The slot index where the SIM card is inserted.
   * If the device has multiple SIM slots, this can be used to
   * determine which slot the SIM belongs to.
   * This may be `undefined` if the slot index cannot be determined.
   */
  slotIndex?: number;
};
