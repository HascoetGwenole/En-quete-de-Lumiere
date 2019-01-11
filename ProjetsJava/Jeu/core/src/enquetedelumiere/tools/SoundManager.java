package enquetedelumiere.tools;

import enquetedelumiere.screens.NetworkScreen;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.sprite.CharacterWithTarget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundManager {


    private Sound footstepSound = Gdx.audio.newSound(Gdx.files.internal("sound/footsteps01.mp3"));

    /** Musique d'ambiance trouvée sur
     * https://tabletopaudio.com/
     */
    private Sound atmosphere = Gdx.audio.newSound(Gdx.files.internal("sound/116_RavenPuff_CommonLow.mp3"));

    private HashMap<String, Character> characters;
    private HashMap<String, Long> charactersVolume;

    private NetworkScreen screen;

    public SoundManager(HashMap<String, Character> characters, NetworkScreen screen){
        this.characters = characters;
        this.screen = screen;
        charactersVolume = new HashMap<>();

        for(Character c :characters.values()){
            long id = footstepSound.play(1.0f);
            charactersVolume.put(c.name, id);
            footstepSound.setLooping(id, true);
        }

        long atmosId = atmosphere.play(.3f);
        atmosphere.setLooping(atmosId, true);
    }

    public void update(){
        if (screen.getActivePlayer() != null){
            characters.values().stream().forEach(c ->{
                if (c instanceof CharacterWithTarget){
                    if (((CharacterWithTarget) c).isMoving()){
                        float distToActivePlayer = c.getPosition().dst(screen.getActivePlayer().getPosition());
                        if(distToActivePlayer > 16 * 20){
                            footstepSound.setVolume(charactersVolume.get(c.name),  0);
                        }else{
                            //System.out.println("Je suis tout près");
                            footstepSound.setVolume(charactersVolume.get(c.name),  20/distToActivePlayer);
                        }
                    }else{
                        footstepSound.setVolume(charactersVolume.get(c.name), 0);
                    }
                }else{
                    if (c.getBody().getLinearVelocity().isZero()){
                        footstepSound.setVolume(charactersVolume.get(c.name), 0);
                    }else{
                        footstepSound.setVolume(charactersVolume.get(c.name),  .4f);
                        if (c.equals(screen.getActivePlayer()))
                            footstepSound.setVolume(charactersVolume.get(c.name),  .2f);
                        else{
                            float distToActivePlayer = c.getPosition().dst(screen.getActivePlayer().getPosition());
                            if(distToActivePlayer > 16 * 20){
                                footstepSound.setVolume(charactersVolume.get(c.name),  0);
                            }else{
                                footstepSound.setVolume(charactersVolume.get(c.name),  20/distToActivePlayer);
                            }
                        }
                    }
                }

            });
        }
    }
}
