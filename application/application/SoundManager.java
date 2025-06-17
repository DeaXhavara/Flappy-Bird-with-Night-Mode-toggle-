package application;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private AudioClip dieSound;
    private AudioClip hitSound;
    private AudioClip wingSound;
    private AudioClip swooshingSound;
    private AudioClip pointSound;
    
    public SoundManager(Class<?> resourceClass) {
        try {
            dieSound = new AudioClip(resourceClass.getResource("/resources/sfx_die.mp3").toExternalForm());
            hitSound = new AudioClip(resourceClass.getResource("/resources/sfx_hit.mp3").toExternalForm());
            wingSound = new AudioClip(resourceClass.getResource("/resources/sfx_wing.mp3").toExternalForm());
            swooshingSound = new AudioClip(resourceClass.getResource("/resources/sfx_swooshing.mp3").toExternalForm());
            pointSound = new AudioClip(resourceClass.getResource("/resources/sfx_point.mp3").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading sound resources: " + e.getMessage());
        }
    }
    
    public void playDieSound() {
        if (dieSound != null) dieSound.play();
    }
    
    public void playHitSound() {
        if (hitSound != null) hitSound.play();
    }
    
    public void playWingSound() {
        if (wingSound != null) wingSound.play();
    }
    
    public void playSwooshingSound() {
        if (swooshingSound != null) swooshingSound.play();
    }
    
    public void playPointSound() {
        if (pointSound != null) pointSound.play();
    }
}