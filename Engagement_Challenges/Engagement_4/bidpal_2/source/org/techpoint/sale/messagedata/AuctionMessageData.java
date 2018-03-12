package org.techpoint.sale.messagedata;

/**
 * Class to represent all the types of messages with so little
 * data that it's not worth making their own class.
 * Also parent class for all BidPalMessageData classes 
 */
public abstract class AuctionMessageData {
	public enum MessageType {AUCTION_START, BID_RECEIPT, BID_COMMITMENT, BID_COMPARISON, BIDDING_OVER, CLAIM_WIN, CONCESSION, AUCTION_END};
	
	protected static AuctionSerializer serializer;
	public final MessageType type;
	
	public String auctionId;
	
	public AuctionMessageData(MessageType type, String auctionId){
		this.type = type;
		this.auctionId = auctionId;
	}
	
	public static void defineSerializer(AuctionSerializer serialer){
		serializer = serialer;
	}
	
	public String getAuctionId(){
		return auctionId;
	}
	
	/////////////////////////  simple subclasses are defined below /////////////////////////
	
	
	public static class AuctionStart extends AuctionMessageData{
		public String description;
		public AuctionStart(String id, String desc){
			super(MessageType.AUCTION_START, id);
			description = desc;
		}
	}

	public static class ProposalReceipt extends AuctionMessageData{
		public ProposalReceipt(String id){super(MessageType.BID_RECEIPT, id);}
	}

	public static class BiddingOver extends AuctionMessageData{
		public BiddingOver(String id){
			super(MessageType.BIDDING_OVER, id);
		}
	}
	
	public static class Concession extends AuctionMessageData{
		public Concession(String id){
			super(MessageType.CONCESSION, id);
		}
	}
	
	public static class AuctionEnd extends AuctionMessageData{
		public String winner;
		public int winningProposal;
		public AuctionEnd(String id, String winner, int winningProposal){
			super(MessageType.AUCTION_END, id);
			this.winner = winner;
			this.winningProposal = winningProposal;
		}
	}
}

