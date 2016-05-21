package saga.progetto.tesi.entity;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import saga.progetto.tesi.map.GameMap;

public abstract class PhysicsEntity extends Entity
{
	private Body body;
	private float x;
	private float y;
	private GameMap map;
	
	public PhysicsEntity(float x, float y, GameMap map)
	{
		this.x = x;
		this.y = y;
		this.map = map;
	}
	
	public enum Material
	{
		DEFAULT(1.00f,  0.30f), METAL(7.85f,  0.20f), STONE(2.40f,  0.50f), WOOD(0.53f,  0.40f),
		GLASS(2.50f,  0.10f), RUBBER(1.50f,  0.80f), ICE(0.92f,  0.01f), FIRE(2.0f,  0.5f), 
		PUMICE(0.25f,  0.60f), POLYSTYRENE(0.10f,  0.60f), FABRIC(0.03f,  0.60f),  PAPER(0.1f, 0.5f),
		SPONGE(0.018f, 0.90f), AIR(0.001f, 0.90f), HELIUM(0.0001f, 0.9f), LIVING(1.00f, 0f), 
		DAGGER(1.00f, 0f), SWORD(3.5f, 0f), SPEAR(0f, 0f), AXE(5.0f, 0f),
		SHIELD(2.00f, 0f);
		   
		private float density;
		private float friction;
	
		
		Material(float density, float friction)
		{
			this.density = density;
			this.friction = friction;
		}

		public float getDensity()
		{
			return density;
		}

		public float getFriction()
		{
			return friction;
		}
	}
	
	public void initPhysicalBody(BodyType type, float width, float height, Material material, int filtering)
	{
		initPhysicalBody(type, width, height, material, filtering, 0);
	}
	
	public void initPhysicalBody(BodyType type, float width, float height, Material material, int bits, int mask)
	{
		BodyDef bd = new BodyDef();
		bd.type = type;
		bd.position.set(x / GameMap.PTM_RATIO, y / GameMap.PTM_RATIO);
		bd.gravityScale = 0;
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(width / GameMap.PTM_RATIO / 2, height/ GameMap.PTM_RATIO / 2);
		FixtureDef fd = new FixtureDef();
		fd.density = material.getDensity();
		fd.friction = material.getFriction();
		fd.shape = ps;
		if (mask == 0)
			fd.filter.groupIndex = bits;
		else
		{
			fd.filter.categoryBits = bits;
			fd.filter.maskBits = mask;
		}
		
		body = map.getWorld().createBody(bd);
		body.createFixture(fd);
		map.getBodies().add(body);
	}
	
	public void setSensor(boolean isSensor)
	{
		body.getFixtureList().setSensor(true);
	}
	
	public void initRadialBody(BodyType type, float radius, Material material, int bits, int mask)
	{
		BodyDef bd = new BodyDef();
		bd.type = type;
		bd.position.set(x / GameMap.PTM_RATIO, y / GameMap.PTM_RATIO);
		bd.gravityScale = 0;
		CircleShape cs = new CircleShape();
		cs.setRadius(radius / GameMap.PTM_RATIO);
		FixtureDef fd = new FixtureDef();
		fd.density = material.getDensity();
		fd.friction = material.getFriction();
		fd.shape = cs;
		if (mask == 0)
			fd.filter.groupIndex = bits;
		else
		{
			fd.filter.categoryBits = bits;
			fd.filter.maskBits = mask;
		}
		body = map.getWorld().createBody(bd);
		body.createFixture(fd);
		map.getBodies().add(body);
	}

	public Body getBody()
	{
		return body;
	}
	
	public float x()
	{
		return Math.round(body.getPosition().x * GameMap.PTM_RATIO);
	}
	
	public float y()
	{
		return Math.round(body.getPosition().y * GameMap.PTM_RATIO);
	}
	
	public void setX(float x)
	{
		this.x = x;
	}

	public void setY(float y)
	{
		this.y = y;
	}
	
	public float getPhysicsX()
	{
		return body.getPosition().x;
	}

	public float getPhysicsY()
	{
		return body.getPosition().y;
	}

	public void setActive(boolean flag)
	{
		body.setActive(flag);
	}
	
	public boolean isActive()
	{
		return body.isActive();
	}

	public World getWorld()
	{
		return map.getWorld();
	}

	public void setWorld(World world)
	{
		map.setWorld(world);
	}

	public GameMap getMap()
	{
		return map;
	}

	public void setMap(GameMap map)
	{
		this.map = map;
	}
	
	public void setVelocity(float x, float y)
	{
		body.setLinearVelocity(new Vec2(x, y));
	}
	
	public void clear()
	{
		map.getBodies().remove(body);
		map.getWorld().destroyBody(body);
	}
}
