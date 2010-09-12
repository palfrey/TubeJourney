package net.tevp.tubejourney;

import java.util.Vector;
import net.tevp.journeyplannerparser.Journey;

public interface JourneyTaskHandler
{
	public void addProgressText(final String text);
	public void journeyComplete(Vector<Journey> js);
}
