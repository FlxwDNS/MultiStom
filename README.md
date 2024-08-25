# MultiStom

## Create a task
```java
@MultiStomTaskEnvironment(name = "LobbyTask", authors = {"flxwdns"})
public final class LobbyTask extends MultiStomTask {

    @Override
    public void spaceState(MultiStomSpace space, MultiStomSpaceState state) {
        if(state.equals(MultiStomSpaceState.DISCONNECTED)) {
            return;
        }

        var instance = MultiStom.instance().instanceFactory().create(space);
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        MultiStom.instance().eventFactory().listen(space, PlayerSpawnEvent.class, event -> {
            event.getPlayer().teleport(new Pos(0, 42, 0));
        });

        MultiStom.instance().eventFactory().listen(space, PlayerSpawnEvent.class, event -> {
            event.getPlayer().sendMessage("Welcome to the lobby!");
        });

        space.spawnInstance(instance);
    }
}
```

## Crate a template
Just create a folder in the existing `tasks` folder.
After you restart MultiStom a config should be created.

Now you can just put your Tasks into the folder.
![image](https://github.com/user-attachments/assets/66be7beb-3319-456a-a479-bad64838bc70)
