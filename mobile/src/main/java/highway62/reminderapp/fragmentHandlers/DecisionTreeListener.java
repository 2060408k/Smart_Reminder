package highway62.reminderapp.fragmentHandlers;

import highway62.reminderapp.constants.EventType;
import highway62.reminderapp.constants.HandlerType;

/**
 * Created by Highway62 on 11/08/2016.
 */
public interface DecisionTreeListener {
    void loadFragmentHandler(HandlerType handlerType, EventType type);
}
