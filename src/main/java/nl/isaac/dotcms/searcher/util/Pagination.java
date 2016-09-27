package nl.isaac.dotcms.searcher.util;

public class Pagination {

	private boolean hasNext;
	private boolean hasPrevious;
	
	private int currentPage;
	private int total;
	private int begin;
	private int end;
	private int totalPages;
	
	public Pagination(int currentPage, int totalPages, int total, int begin, int end) {
		super();
		this.hasNext = currentPage < totalPages;
		this.hasPrevious = currentPage != 1;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.total = total;
		this.begin = begin;
		this.end = end;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public boolean isHasPrevious() {
		return hasPrevious;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getTotal() {
		return total;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public int getTotalPages() {
		return totalPages;
	}
	
}
