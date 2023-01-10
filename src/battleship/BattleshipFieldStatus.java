package battleship;

/**
 * The BattleshipFieldStatus enumeration is used to describe the
 * status of a field on a battleship board.
 *
 * @author friedrichvoelkers
 */
public enum BattleshipFieldStatus {
    EMPTY_NO_SHOOT, // On this field is no ship and your enemy didn't shoot this field
    EMPTY_SHOOT,    // On this field is no ship and your enemy shoot this field
    SHIP_NO_SHOOT,  // On this field is one of your ships and your enemy didn't found this
    SHIP_SHOOT,     // On this field is one of your ships and your enemy found this
    UNKNOWN,        // Fields from your enemy you didn't shoot
}
