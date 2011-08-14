package pvpworldserver;

import java.awt.Point;

public class Character 
{
	private int id;
	private int currentMapID;
	private Point position;
	private PhysicsVector velocity;
	private PhysicsVector acceleration;
	private Inventory inventory;
	public Character(int id,int currentMapID,Point position,PhysicsVector velocity,PhysicsVector acceleration,Inventory inventory)
	{
		this.id = id;
		this.currentMapID = currentMapID;
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.inventory = inventory;
	}
}
