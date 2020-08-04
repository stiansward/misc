package oblig5;

public class IntList {
	int[] data;
	int len = 0;

	IntList(int len) {
		data = new int[Math.max(2, len)];
	}

	IntList() {
		data = new int[16];
	}

	void add(int elem) {
		if (len == data.length) {
			int[] b = new int[data.length * 2];
			System.arraycopy(data, 0, b, 0, len);
			data = b;
		}
		data[len++] = elem;
	}

	void append(IntList other) {
		if (len + other.len > data.length) {
			int newLen = Math.max(2 * len, len + 2 * other.len);
			int[] b = new int[newLen];
			System.arraycopy(data, 0, b, 0, len);
			data = b;
		}
		System.arraycopy(other.data, 0, data, len, other.len);
		len += other.len;
	}

	void clear() {
		len = 0;
	}

	int get(int pos) {
		if (pos > len - 1)
			return -1;
		else
			return data[pos];
	}

	int size() {
		return len;
	}
}