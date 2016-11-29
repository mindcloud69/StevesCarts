package stevesvehicles.api.network;

import java.io.IOException;

public interface IStreamable {

	void writeData(DataWriter data) throws IOException;

	void readData(DataReader data) throws IOException;
}
