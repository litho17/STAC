package org.digitalapex.powerbroker;

import org.digitalapex.talkers.TalkersPublicIdentity;

/**
 * An auction paired with the person holding it
 */
public class CommodityGoBetweenSelloff implements Comparable<CommodityGoBetweenSelloff> {
    public final String id;
    public final TalkersPublicIdentity seller;
    public final int commodityAmount;

    public CommodityGoBetweenSelloff(String id, TalkersPublicIdentity seller, int commodityAmount) {
        this.id = id;
        this.seller = seller;
        this.commodityAmount = commodityAmount;
    }

    @Override
    public int compareTo(CommodityGoBetweenSelloff other) {
        if (other.commodityAmount != commodityAmount) {
            return Integer.compare(commodityAmount, other.commodityAmount);
        }
        if (!other.id.equals(id)) {
            return id.compareTo(other.id);
        }
        if (!other.seller.equals(seller)) {
            return seller.compareTo(other.seller);
        }
        return 0; // they're equal
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommodityGoBetweenSelloff that = (CommodityGoBetweenSelloff) o;

        if (commodityAmount != that.commodityAmount) return false;
        if (!id.equals(that.id)) return false;
        return seller.equals(that.seller);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + seller.hashCode();
        result = 31 * result + commodityAmount;
        return result;
    }

    @Override
    public String toString() {
        return "PowerBrokerAuction{" +
                "id='" + id + '\'' +
                ", seller=" + seller +
                ", powerAmount=" + commodityAmount +
                '}';
    }
}
