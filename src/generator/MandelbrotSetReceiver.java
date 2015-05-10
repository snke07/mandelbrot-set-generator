package generator;

import java.util.List;

public interface MandelbrotSetReceiver {
    public void receiveSet(List<MandelbrotNumber> numbers);
}
