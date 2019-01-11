package enquetedelumiere.tools;

import com.badlogic.gdx.physics.box2d.World;

public class DialogueInteractiveBox extends InteractiveBox {

	private String content;
	private String title;
	private boolean activeClue = true;
	public String name;

	public DialogueInteractiveBox(World world, float x, float y, float width, float height, CollisionType type,
			String content, String title) {
		super(world, x, y, width, height, type);
		this.content = content;
		this.title = title;
	}

	public synchronized String getContent() {
		return content;
	}

	public synchronized String getTitle() {
		return title;
	}

	public synchronized boolean isActiveClue() {
		return activeClue;
	}

	public synchronized void setActiveClue(boolean activeClue) {
		this.activeClue = activeClue;
	}

}
