# JSON and Gson

## Idea

JSON is the text format of the answers. Gson turns JSON text into a Java object (a DTO) and back. A DTO repeats the shape of the JSON. The model of the app (`Breed`) is a separate class, and a mapper turns a DTO into the model.

## Roles

| Role | What it does | In this project |
|---|---|---|
| JSON text | The body of the answer | `core/src/test/resources/*.json` (saved answers) |
| DTO | A class with the shape of the JSON | `BreedListResponse`, `ImagesResponse`, `ErrorDto`, `PostDto` in `core` |
| `Gson` | Reads and writes JSON | Comes with `converter-gson` |
| Mapper | DTO to app model | `BreedMapper` |

## How it works

Two answers of Dog CEO have different shapes:

```
 GET /api/breeds/list/all
 {"message": {"affenpinscher": [], "african": ["wild"], "hound": ["afghan", "basset", …]},
  "status": "success"}

 GET /api/breed/hound/images/random/3
 {"message": ["https://images.dog.ceo/breeds/hound-afghan/….jpg", …],
  "status": "success"}
```

The list of breeds is an object (`Map<String, List<String>>`), not an array. The key `message` is not a good Java name, so the field has `@SerializedName("message")`:

```java
public class BreedListResponse {
    @SerializedName("message")
    public Map<String, List<String>> breeds;   // breed -> sub-breeds
    public String status;
}
```

Trap: `GET /breed/{breed}/images/random` (no number) returns a string in `message`, and `/images/random/3` returns an array. One DTO does not fit both.

| In the JSON | In the class | Result of Gson |
|---|---|---|
| Key is present | Field is present | Value is set |
| Key is missing | Field is present | The field keeps its default: `null`, `0`, `false` |
| Key is present | No field | The key is skipped, no error |
| `"message"` | `@SerializedName("message") imageUrls` | The Java name differs from the JSON name |
| A string | Field `List<String>` | `JsonSyntaxException` |

`BreedMapper` makes `Breed` objects and sorts them by name. `Breed` is immutable and copies the list of sub-breeds. If the server changes its format, only the DTO and the mapper change.

jsonschema2pojo can make a draft class from a JSON sample. Check the types and names by hand.

## Where to look

- `core/…/dto/` — the four DTO classes.
- `core/src/test/…/dto/DogResponseParsingTest` — parsing of saved answers, no network.
- `core/src/test/…/data/BreedMapperTest` — sorting, null map, copy of the list.
- `retrofit-basic` screen `Mistakes`: modes `Wrong DTO shape` and `Wrong field name`.

## Advantages and disadvantages

Advantages:
- A DTO is plain Java. No code generation.
- The app model does not depend on the server format.

Disadvantages:
- Gson does not report a missing key. The field stays `null` and the error comes later.
- Gson reads by reflection. With R8 in a release build, keep rules for the DTO classes (this project does not use R8).
